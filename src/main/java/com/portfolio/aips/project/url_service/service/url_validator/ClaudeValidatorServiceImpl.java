package com.portfolio.aips.project.url_service.service.url_validator;


import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;


@Service("ClaudeValidator")
@Slf4j
public class ClaudeValidatorServiceImpl implements UrlValidatorService{


    private final WebClient webClient;

    @Qualifier("DefaultValidator")
    private final UrlValidatorService defaultValidatorService;

    public ClaudeValidatorServiceImpl(WebClient webClient, @Qualifier("DefaultValidator") UrlValidatorService defaultValidatorService) {
        this.webClient = webClient;
        this.defaultValidatorService = defaultValidatorService;
    }


    @Override
    @Async
    public CompletableFuture<String> requestHTTP(String url) {


        log.info("Cluade HTTP Request");
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new CustomException(ErrorCode.ARCHIVE_URL_UNREACHABLE)))
                )
                // 압축 여부 상관없이 raw bytes로 읽기
                .bodyToMono(byte[].class)
                .map(bytes -> {
                    // Content-Encoding 확인 후 수동 디코딩 가능
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .onErrorMap(WebClientResponseException.class, e -> e)
                .onErrorMap(Exception.class, e -> new CustomException(ErrorCode.ARCHIVE_URL_UNREACHABLE))
                .toFuture();

//        HttpHeaders headers = new HttpHeaders();
//        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
//        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//
//        HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//
//        if(!response.getStatusCode().is2xxSuccessful())
//        {
//            throw new CustomException(ErrorCode.ARCHIVE_URL_UNREACHABLE);
//        }
//
//        return CompletableFuture.completedFuture(response.getBody());
    }

    @Override
    public boolean validProc(String url, String body) {

        return defaultValidatorService.validProc(url, body);
    }
}
