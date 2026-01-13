
### Virtual Thread 관련 정리 내용 링크
- [본인 VELOG 주소](https://velog.io/@terace/%EB%B2%84%EC%B8%84%EC%96%BC-%EC%8A%A4%EB%A0%88%EB%93%9C%EC%99%80-%EC%84%B8%EB%A7%88%ED%8F%AC%EC%96%B4)


# Semaphore 설정 가이드 - DB와 Elasticsearch

## 개요

이 문서는 Spring Boot 애플리케이션에서 Database와 Elasticsearch에 대한 동시 접근을 제어하기 위해 Semaphore를 사용하는 방법과 그 설정 근거를 설명합니다.

---

## Semaphore Bean 설정

### 코드 구성

```java
@Configuration
public class SemaphoreConfig {
    
    @Bean(name = "dbSemaphore")
    public Semaphore dbSemaphore() {
        return new Semaphore(15);
    }
    
    @Bean(name = "esSemaphore")
    public Semaphore esSemaphore() {
        return new Semaphore(8);
    }
}
```

---

## Database Semaphore 설정

### 설정값: 15개 Permit

```java
@Bean(name = "dbSemaphore")
public Semaphore dbSemaphore() {
    return new Semaphore(15);
}
```

### 설정 근거

#### 1. DB Connection Pool과의 매칭
Database의 최대 커넥션 풀 크기가 15로 설정되어 있습니다.

```yaml
# application.yml 예시
spring:
  datasource:
    hikari:
      maximum-pool-size: 15
      minimum-idle: 5
      connection-timeout: 30000
```

**왜 Pool Size와 동일하게 설정하는가?**
- Connection Pool에 15개의 연결만 있는데, 더 많은 스레드가 DB 접근을 시도하면 대기 상태가 발생
- Semaphore로 미리 제어하면 애플리케이션 레벨에서 효율적으로 대기 관리 가능
- Connection Pool의 타임아웃을 줄이고, 더 나은 에러 처리 가능

#### 2. 리소스 보호
```
Virtual Threads (수백~수천 개)
         ↓
    Semaphore (15)
         ↓
  Connection Pool (15)
         ↓
     Database
```

**흐름 설명:**
1. Virtual Thread는 수천 개가 동시 실행 가능
2. Semaphore가 15개만 DB 접근 허용
3. Connection Pool도 정확히 15개 제공
4. Database는 과부하 없이 안정적으로 작동

#### 3. 실제 사용 예시

```java
@Service
public class UserService {
    
    @Autowired
    @Qualifier("dbSemaphore")
    private Semaphore dbSemaphore;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public User findUserById(Long id) {
        try {
            dbSemaphore.acquire();
            try {
                return jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE id = ?",
                    new Object[]{id},
                    new UserRowMapper()
                );
            } finally {
                dbSemaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("DB 접근 대기 중 인터럽트 발생", e);
        }
    }
}
```

#### 4. 장점
- ✅ **Connection Pool 고갈 방지**: 대기 스레드 수를 제어
- ✅ **타임아웃 관리 개선**: 애플리케이션 레벨에서 대기 제어
- ✅ **리소스 효율성**: 불필요한 Connection 대기 제거
- ✅ **에러 핸들링**: 명확한 동시성 제어로 예측 가능한 동작

---

## Elasticsearch Semaphore 설정

### 설정값: 8개 Permit

```java
@Bean(name = "esSemaphore")
public Semaphore esSemaphore() {
    return new Semaphore(8);
}
```

### Elasticsearch 설정

```yaml
# docker-compose.yml
elasticsearch:
  environment:
    - node.name=es-node
    - cluster.name=es-cluster
    - discovery.type=single-node        # 단일 노드 클러스터
    - bootstrap.memory_lock=true
    - "ES_JAVA_OPTS=-Xms4g -Xmx4g"     # JVM 메모리 4GB
    - xpack.security.enabled=false
    - xpack.security.http.ssl.enabled=false
    - ELASTIC_PASSWORD='1234'
```

### 설정 근거

#### 1. Elasticsearch 처리 용량 고려

**단일 노드 + 4GB 메모리 기준**
- Elasticsearch는 I/O와 CPU를 모두 사용하는 하이브리드 워크로드
- 단일 노드에서 동시 처리 가능한 효율적인 요청 수는 제한적
- 너무 많은 동시 요청은 오히려 성능 저하 유발

#### 2. 최적 동시 요청 수 계산

```
권장 동시 요청 수 = CPU 코어 수 × (1~2)
```

**일반적인 서버 환경:**
- 4 코어 CPU: 4~8 동시 요청
- 8 코어 CPU: 8~16 동시 요청

**4GB 메모리 고려:**
- ES는 JVM 힙 메모리(4GB) + 시스템 캐시 필요
- 동시 요청이 많으면 GC 압박 증가
- 8개 정도가 안정적인 처리량 제공

#### 3. 스레드 풀과의 관계

Elasticsearch는 내부적으로 여러 Thread Pool을 운영합니다:

```
search thread pool: 
  - size = CPU 코어 × 1.5
  - queue_size = 1000

index thread pool:
  - size = CPU 코어 수
  - queue_size = 200
```

**Semaphore 8의 의미:**
- ES 내부 Thread Pool이 포화되지 않도록 외부 요청 제한
- Queue에 쌓이는 요청을 애플리케이션에서 미리 제어

#### 4. 실제 사용 예시

```java
@Service
public class SearchService {
    
    @Autowired
    @Qualifier("esSemaphore")
    private Semaphore esSemaphore;
    
    @Autowired
    private ElasticsearchClient esClient;
    
    public SearchResponse<Product> searchProducts(String query) {
        try {
            esSemaphore.acquire();
            try {
                return esClient.search(s -> s
                    .index("products")
                    .query(q -> q
                        .match(m -> m
                            .field("name")
                            .query(query)
                        )
                    ),
                    Product.class
                );
            } finally {
                esSemaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("ES 접근 대기 중 인터럽트 발생", e);
        }
    }
}
```

#### 5. 장점
- ✅ **ES 과부하 방지**: 단일 노드가 감당할 수 있는 수준으로 제한
- ✅ **안정적인 응답 시간**: 요청이 몰려도 일정한 성능 유지
- ✅ **메모리 관리**: GC 압박 감소로 안정적인 메모리 사용
- ✅ **타임아웃 감소**: ES 내부 Queue 대기 시간 단축

---

## 설정값 조정 가이드

### Database Semaphore 조정

#### 상황별 설정값

| 상황 | Connection Pool | Semaphore | 이유 |
|------|----------------|-----------|------|
| 기본 | 15 | 15 | 1:1 매칭으로 효율적 관리 |
| 여유 있게 | 20 | 18 | Pool의 90% 사용 |
| 보수적 | 15 | 12 | Pool의 80% 사용 (안전 마진) |
| 읽기 전용 | 10 | 10 | 적은 Connection으로 충분 |

#### 모니터링 지표

```java
// Semaphore 사용률 모니터링
int availablePermits = dbSemaphore.availablePermits();
int queueLength = dbSemaphore.getQueueLength();

// 경고 조건
if (availablePermits == 0 && queueLength > 10) {
    log.warn("DB Semaphore 포화 상태: 대기 중인 요청 {}", queueLength);
}
```

### Elasticsearch Semaphore 조정

#### 상황별 설정값

| ES 환경 | CPU 코어 | 메모리 | 권장 Semaphore |
|---------|---------|--------|----------------|
| 개발 환경 | 2 코어 | 2GB | 4 |
| 운영 환경 (소규모) | 4 코어 | 4GB | 8 |
| 운영 환경 (중규모) | 8 코어 | 8GB | 12-16 |
| 클러스터 (3노드) | 8 코어 | 8GB | 24-32 |

#### 성능 테스트로 최적값 찾기

```java
// 부하 테스트 예시
@Test
public void testOptimalSemaphoreSize() {
    for (int permits = 4; permits <= 16; permits += 2) {
        Semaphore testSemaphore = new Semaphore(permits);
        
        long startTime = System.currentTimeMillis();
        // 1000개 요청 실행
        runLoadTest(testSemaphore, 1000);
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.printf("Permits: %d, Duration: %dms%n", permits, duration);
    }
}
```


## 요약

| 항목 | 설정값 | 근거 |
|------|--------|------|
| **DB Semaphore** | 15 | Connection Pool Max Size와 동일하게 설정 |
| **ES Semaphore** | 8 | 단일 노드 4GB 메모리 기준 최적 동시 요청 수 |
| **설정 원칙** | 리소스 용량에 맞춰 조정 | 과부하 방지 및 안정적인 성능 확보 |


### 핵심 포인트
- ✅ Semaphore는 백엔드 리소스 용량에 맞춰 설정
- ✅ Virtual Thread는 많이 생성해도 Semaphore로 제어
- ✅ 모니터링으로 지속적인 튜닝 필요
- ✅ 부하 테스트로 최적값 검증