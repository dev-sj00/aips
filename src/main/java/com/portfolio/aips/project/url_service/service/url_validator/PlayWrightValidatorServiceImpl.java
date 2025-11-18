package com.portfolio.aips.project.url_service.service.url_validator;


import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.utils.enums.LLMUrlPrefix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service("PlayWrightValidator")
@RequiredArgsConstructor
@Slf4j
public class PlayWrightValidatorServiceImpl implements UrlValidatorService{

    private final WebClient webClient;
    @Override
    @Async
    public CompletableFuture<String> requestHTTP(String url) {


        String serverUrl = "http://localhost:3000/check-page";

        // 요청 바디 생성 (RestTemplate 코드와 동일한 구조)
        LLMUrlPrefix llmUrlPrefix = LLMUrlPrefix.valueOf(LLMUrlPrefix.findKeyByUrl(url));
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("url", url);
        requestBody.put("type", llmUrlPrefix.name());



        return webClient.post()
                .uri(serverUrl)
                // 1. HTTP 헤더 설정 (Content-Type: application/json)
                .contentType(MediaType.APPLICATION_JSON)
                // 2. 요청 본문 설정
                .bodyValue(requestBody)
                .retrieve()

                // 3. HTTP 4xx, 5xx 에러 처리
                .onStatus(status -> !status.is2xxSuccessful(), response -> {
                    HttpStatus statusCode = (HttpStatus) response.statusCode();
                    log.info("Response Status Code (non-2xx): {}", statusCode);

                    // 응답 본문을 읽어와 로그를 남기거나 추가 처리를 할 수 있도록 flatMap 사용 (선택 사항)
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("Error response body: {}", body);

                                // ⭐️ 404 NOT_FOUND 일 경우 무조건 CustomException 발생
                                if (statusCode == HttpStatus.NOT_FOUND) {
                                    return Mono.error(new CustomException(ErrorCode.ARCHIVE_URL_UNREACHABLE));
                                }

                                // "INVALID" 응답 본문을 체크하던 기존 로직 제거 (선택 사항: 필요하면 다시 추가)

                                // 그 외 4xx/5xx 에러 (404 제외)
                                return Mono.error(new CustomException(ErrorCode.ARCHIVE_URL_UNREACHABLE));
                            });
                })

                // 4. 응답 본문 추출 (Mono<String> 반환)
                .bodyToMono(String.class)

                // 5. 성공적인 응답 본문을 처리
                .map(body -> {
                    // 2xx 상태 코드를 받은 경우
                    log.info(body);
                    return "ok"; // CompletableFuture<String>의 성공 값
                })

                // 6. Mono 체인 외부의 일반적인 에러 (DNS 실패, 연결 시간 초과 등) 처리
                // WebClientResponseException은 onStatus에서 처리되지 않은 4xx/5xx 응답 시 발생
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.error("HTTP Status Code Exception: {}", e.getMessage());
                    // 404를 포함한 모든 HTTP 상태 코드 에러 시 동일한 CustomException 반환
                    return new CustomException(ErrorCode.ARCHIVE_URL_UNREACHABLE);
                })
                .onErrorMap(Exception.class, e -> {
                    log.error("General WebClient Error: {}", e.getMessage());
                    // 그 외 일반적인 연결 오류 시 CustomException 반환
                    return new CustomException(ErrorCode.ARCHIVE_URL_UNREACHABLE);
                })

                // 7. Mono를 CompletableFuture로 변환하여 최종 반환
                .toFuture();



       /* RestTemplate restTemplate = new RestTemplate();

        // 요청할 URL
        String serverUrl = "http://localhost:3000/check-page";

        // 요청 바디

        LLMUrlPrefix llmUrlPrefix = LLMUrlPrefix.valueOf(LLMUrlPrefix.findKeyByUrl(url));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("url", url);
        requestBody.put("type", llmUrlPrefix.name()); // 또는 "gemini"

        // HTTP 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity에 헤더와 바디 넣기
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            // POST 요청
            ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, request, String.class);


            log.info("Status code: " + response.getStatusCode());


            // INVALID / VALID 판단
            if (response.getStatusCode() == HttpStatus.NOT_FOUND || "INVALID".equals(response.getBody())) {
                System.out.println("페이지 없음 또는 오류 발생");
            } else {
                System.out.println("페이지 존재");
            }

        } catch (Exception e) {
            System.out.println("요청 실패: " + e.getMessage());
        }

        return CompletableFuture.completedFuture("ok");*/
    }


    @Override
    public boolean validProc(String url, String body) {
        return true;
    }
}
