package com.portfolio.aips.project.url_service.service.url_validator;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.utils.enums.LLMUrlPrefix;
import com.portfolio.aips.project.utils.enums.LLMValidBodyValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Service("DefaultValidator")
@RequiredArgsConstructor
@Slf4j
//grok3 , gemini
@Qualifier("DefaultValidator")
public class DefaultValidatorServiceImpl implements UrlValidatorService{
    private final WebClient webClient;





    @Override
    @Async
    public CompletableFuture<String> requestHTTP(String url) {

        return webClient.get()
                .uri(url)
                .retrieve()

                // 1. HTTP 4xx, 5xx 에러 처리
                // 2xx 코드가 아닐 경우 CustomException으로 즉시 변환
                .onStatus(HttpStatusCode -> !HttpStatusCode.is2xxSuccessful(), response -> {
                    // 응답 본문을 읽어와 로그를 남기거나 추가 처리를 할 수 있지만, 여기서는 간결하게 예외 발생
                    log.error("response status code: {}", response.statusCode());

                    // ⭐️ throw new CustomException(...)과 동일한 역할을 합니다. ⭐️
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("Error response body: {}", body);
                                return Mono.error(new CustomException(ErrorCode.ARCHIVE_URL_UNREACHABLE));
                            });
                })

                // 2. 응답 본문 추출 (Mono<String> 반환)
                .bodyToMono(String.class)

                // 3. WebClient 자체 오류(DNS 실패, 연결 시간 초과 등) 처리
                .onErrorMap(WebClientResponseException.class, e -> {
                    // HttpStatusCodeException과 유사한 WebClientResponseException 처리
                    log.error("HTTP Status Code Exception: {}", e.getMessage());
                    // 필요한 경우 다른 CustomException으로 변환 가능
                    return e;
                })
                .onErrorMap(Exception.class, e -> {
                    // 그 외 일반적인 연결 오류 처리
                    log.error("General WebClient Error: {}", e.toString());
                    // throw new Exception(...)과 동일한 역할을 합니다.
                    return new CustomException(ErrorCode.ARCHIVE_URL_UNREACHABLE);
                })

                // 4. Mono를 CompletableFuture로 변환하여 최종 반환
                .toFuture();
    }


    /*    try{


            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("response status code: {}", response.getStatusCode());


            if(!response.getStatusCode().is2xxSuccessful())
            {
                log.error("response status code: {}", response.getStatusCode());
                //throw
                throw new CustomException(ErrorCode.ARCHIVE_URL_UNREACHABLE);
            }


            return CompletableFuture.completedFuture(response.getBody());

        }catch (HttpStatusCodeException e)
        {
            log.error(e.getMessage());
            //throw

        } catch (Exception e) {
            log.error(String.valueOf(e));
            //throw
        }

        return CompletableFuture.completedFuture(null);*/


    @Override
    public boolean validProc(String url, String body) {
        LLMUrlPrefix llmUrlPrefix = LLMUrlPrefix.valueOf(LLMUrlPrefix.findKeyByUrl(url));

        LLMValidBodyValue llmValidBodyValue = LLMValidBodyValue.valueOf(llmUrlPrefix.name());
        return !body.contains(llmValidBodyValue.getValue()); // body에 해당 llm urlPrefix 값이 없어야함
    }


}
