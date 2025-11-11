package com.portfolio.aips.project.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlValidatorUtils {

    private final RestTemplate restTemplate;


    public boolean isUrlAccessible(String url) {
        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    url,                  // 요청 URL
                    HttpMethod.GET,       // HTTP 메서드 (GET, POST, PUT, DELETE 등)
                    null,
                    String.class          // 응답 바디 타입
            );

            return response.getStatusCode().is2xxSuccessful();

        }catch (HttpStatusCodeException e)
        {

            return false;

        } catch (Exception e) {
            log.error(String.valueOf(e));
            return false;
        }
    }
}
