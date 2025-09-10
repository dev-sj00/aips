# TokenRefreshService.java - refreshAccessToken() 



### 1. refreshAccessToken() - 메인 메서드
```java
public String refreshAccessToken(String registrationId, String principalName)
```

**파라미터:**
- `registrationId`: OAuth2 제공자 식별자 (예: "google", "github")
- `principalName`: 사용자 식별자

**동작 과정:**
1. 저장된 인증 클라이언트 조회
2. 클라이언트가 없으면 예외 발생
3. 현재 액세스 토큰 추출
4. 토큰 만료 여부 확인
5. 만료된 경우 토큰 갱신, 아니면 기존 토큰 반환

**주요 로직 분석:**
```java
OAuth2AuthorizedClient authorizedClient = (OAuth2AuthorizedClient) Optional
    .ofNullable(authorizedClientService.loadAuthorizedClient(registrationId, principalName))
    .orElseThrow(() -> new IllegalArgumentException("인증된 클라이언트를 찾을 수 없습니다."));
```
- Optional을 사용한 null 체크와 예외 처리

```java
boolean isExpired = Optional.ofNullable(accessToken.getExpiresAt())
    .map(expiresAt -> expiresAt.isBefore(Instant.now()))
    .orElse(false);
```
- 람다와 Optional을 활용한 만료 시간 체크
- null-safe한 방식으로 구현

### 2. refreshAccessTokenRequest() - 토큰 갱신 처리
```java
private String refreshAccessTokenRequest(String registrationId, String principalName, 
                                        OAuth2AuthorizedClient authorizedClient)
```

**동작 과정:**
1. OAuth2AuthorizeRequest 객체 생성
2. AuthorizedClientManager를 통한 토큰 갱신 요청
3. 갱신 실패시 SecurityException 발생
4. 성공시 새로운 액세스 토큰 값 반환

**핵심 코드:**
```java
OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
    .withClientRegistrationId(registrationId)
    .principal(principalName)
    .build();
```
- 빌더 패턴을 사용한 요청 객체 생성

### 3. executeWithExceptionHandling() - 예외 처리 헬퍼
```java
private String executeWithExceptionHandling(Supplier<String> supplier)
```

**특징:**
- 함수형 프로그래밍 방식 (Supplier 인터페이스 활용)
- 중앙화된 예외 처리
- SecurityException은 그대로 전파, 기타 예외는 SecurityException으로 래핑

## 🔄 동작 흐름도

```
사용자 요청
    ↓
refreshAccessToken() 호출
    ↓
인증 클라이언트 조회
    ↓
토큰 존재 여부 확인
    ├─ No → IllegalArgumentException
    └─ Yes ↓
        토큰 만료 체크
        ├─ 만료되지 않음 → 기존 토큰 반환
        └─ 만료됨 ↓
            executeWithExceptionHandling() 호출
                ↓
            refreshAccessTokenRequest() 실행
                ↓
            새 토큰 발급 요청
                ├─ 실패 → SecurityException
                └─ 성공 → 새 토큰 반환
```
