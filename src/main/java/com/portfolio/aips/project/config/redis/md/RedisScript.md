# Redis Script (EVAL/EVALSHA) @Bean 관리 가이드

## 개요

Redis 스크립트를 실행할 때 `EVAL`과 `EVALSHA` 명령어를 사용할 수 있습니다. Spring Data Redis에서는 이러한 스크립트를 효율적으로 관리하기 위해 `RedisScript` 객체를 **@Bean으로 등록하여 관리하는 것을 권장**합니다.

## EVAL vs EVALSHA

### EVAL 명령어
- **특징**: 스크립트 전체를 매번 Redis 서버로 전송
- **단점**:
    - 네트워크 대역폭 낭비
    - 스크립트가 클 경우 성능 저하
    - 매번 전체 스크립트를 전송해야 함

### EVALSHA 명령어
- **특징**: 스크립트의 SHA1 해시값만 전송
- **장점**:
    - 네트워크 대역폭 절약
    - 성능 향상
    - Redis 서버가 스크립트를 캐시에 저장하고 재사용
- **작동 방식**:
    1. `SCRIPT LOAD`로 스크립트를 Redis 서버에 미리 로드
    2. 반환된 SHA1 해시를 사용하여 `EVALSHA` 실행

## Spring Data Redis의 최적화 전략

Spring Data Redis의 `ScriptExecutor`는 자동으로 다음과 같이 최적화합니다:

1. 스크립트의 SHA1 해시를 계산
2. 먼저 `EVALSHA`로 실행 시도
3. 스크립트가 캐시에 없으면 (`NOSCRIPT` 에러 발생)
4. 자동으로 `EVAL`로 폴백하여 실행

## @Bean으로 관리해야 하는 이유

### 1. SHA1 재계산 방지

```java
// ❌ 나쁜 예: 매번 새로운 인스턴스 생성
public boolean checkAndSet(String key, String expected, String newValue) {
    DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
    script.setScriptText("...");
    script.setResultType(Boolean.class);
    // SHA1이 매번 재계산됨
    return redisTemplate.execute(script, keys, args);
}

// ✅ 좋은 예: 싱글톤 Bean으로 관리
@Bean
public RedisScript<Boolean> checkAndSetScript() {
    DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
    script.setScriptSource(new ResourceScriptSource(
        new ClassPathResource("scripts/checkandset.lua")
    ));
    script.setResultType(Boolean.class);
    return script;
    // SHA1이 한 번만 계산되고 재사용됨
}
```

### 2. 메모리 효율성

`DefaultRedisScript`의 단일 인스턴스를 애플리케이션 컨텍스트에서 설정하면 **스크립트 실행마다 SHA1을 재계산하는 것을 방지**할 수 있습니다.

### 3. 코드 재사용성 및 유지보수성

스크립트를 중앙에서 관리하면 코드 중복을 줄이고 유지보수가 용이합니다.

## 구현 예제

### 1. Lua 스크립트 파일 작성

**src/main/resources/META-INF/scripts/checkandset.lua**
```lua
-- Check and Set 스크립트
local current = redis.call('GET', KEYS[1])
if current == ARGV[1] then
    redis.call('SET', KEYS[1], ARGV[2])
    return true
end
return false
```

### 2. RedisScript Bean 등록

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisScriptConfiguration {

    @Bean
    public RedisScript<Boolean> checkAndSetScript() {
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(
            new ClassPathResource("META-INF/scripts/checkandset.lua")
        ));
        script.setResultType(Boolean.class);
        return script;
    }
    
    // 다른 방법: 인라인 스크립트
    @Bean
    public RedisScript<Long> incrementScript() {
        String scriptText = """
            local current = redis.call('GET', KEYS[1])
            if not current then
                current = 0
            end
            local newValue = current + ARGV[1]
            redis.call('SET', KEYS[1], newValue)
            return newValue
            """;
        
        return RedisScript.of(scriptText, Long.class);
    }
}
```

### 3. 서비스에서 사용

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RedisScriptService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Boolean> checkAndSetScript;

    @Autowired
    public RedisScriptService(
            RedisTemplate<String, String> redisTemplate,
            RedisScript<Boolean> checkAndSetScript) {
        this.redisTemplate = redisTemplate;
        this.checkAndSetScript = checkAndSetScript;
    }

    public boolean checkAndSet(String key, String expectedValue, String newValue) {
        List<String> keys = Collections.singletonList(key);
        return redisTemplate.execute(
            checkAndSetScript, 
            keys, 
            expectedValue, 
            newValue
        );
    }
}
```

## 반환 타입

`RedisScript`의 `resultType`은 다음 중 하나여야 합니다:

- `Long` - 숫자 반환값
- `Boolean` - 참/거짓 반환값
- `List` - 리스트 반환값
- 역직렬화 가능한 값 타입
- `null` - 상태 반환만 하는 경우 (예: "OK")

## 트랜잭션 및 파이프라인 지원

스크립트는 `SessionCallback` 내에서 트랜잭션이나 파이프라인의 일부로 실행될 수 있습니다.

```java
List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
    @Override
    public List<Object> execute(RedisOperations operations) throws DataAccessException {
        operations.multi();
        operations.execute(checkAndSetScript, keys, args);
        // 다른 작업들...
        return operations.exec();
    }
});
```

## 공식 문서 참고

- **Spring Data Redis 공식 문서**: [Redis Scripting](https://docs.spring.io/spring-data/redis/docs/2.4.5/reference/html/#scripting)
- **Redis 공식 문서**:
    - [EVAL 명령어](https://redis.io/docs/latest/commands/eval/)
    - [EVALSHA 명령어](https://redis.io/docs/latest/commands/evalsha/)
    - [Redis Scripting Introduction](https://redis.io/docs/latest/develop/programmability/eval-intro/)

## 베스트 프랙티스

1. **항상 스크립트를 @Bean으로 등록**: SHA1 재계산 방지 및 성능 최적화
2. **외부 파일로 스크립트 관리**: 코드와 스크립트 분리로 유지보수성 향상
3. **적절한 반환 타입 지정**: 타입 안전성 확보
4. **KEYS와 ARGV 올바르게 사용**: Redis 클러스터 환경 고려
5. **스크립트 캐싱 활용**: Redis는 스크립트를 자동으로 캐시하므로 EVALSHA 사용 권장

## 주의사항

- 스크립트는 애플리케이션 로직의 일부이지만 Redis 서버에서 실행됩니다
- 스크립트는 원자적(atomic)으로 실행되므로 긴 실행 시간은 다른 명령어를 블로킹할 수 있습니다
- Redis 7.0 이상에서는 Redis Functions를 사용하는 것도 고려해볼 수 있습니다
- 동적으로 생성되는 스크립트는 안티패턴입니다 - 가능한 한 정적 스크립트를 사용하고 인자로 커스터마이징하세요

## 결론

Redis 스크립트를 Spring Data Redis에서 사용할 때는 `RedisScript` 객체를 **@Bean으로 등록하여 관리**하는 것이 Spring의 권장사항입니다. 이를 통해 성능 최적화, 코드 재사용성, 유지보수성을 모두 향상시킬 수 있습니다.