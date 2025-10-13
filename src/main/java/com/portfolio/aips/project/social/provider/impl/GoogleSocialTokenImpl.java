package com.portfolio.aips.project.social.provider.impl;

import com.portfolio.aips.project.social.provider.SocialTokenProvider;
import com.portfolio.aips.project.social.provider.dto.GoogleTokenResponseDTO;
import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;
import com.portfolio.aips.project.social.provider.enums.TokenStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;



@Service("googleTokenValidator")
@Slf4j
public class GoogleSocialTokenImpl implements SocialTokenProvider {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;



    private final RestTemplate restTemplate = new RestTemplate();



    public SocialTokenValidationResultDTO refreshAccessToken(String refreshToken) {

        try {
            String url = "https://oauth2.googleapis.com/token";

            // Spring의 HttpHeaders 사용
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Spring의 MultiValueMap 사용
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", googleClientId);
            params.add("client_secret", googleClientSecret);
            params.add("refresh_token", refreshToken);
            params.add("grant_type", "refresh_token");

            // Spring의 HttpEntity 사용
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // Spring의 RestTemplate 사용
            ResponseEntity<GoogleTokenResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, GoogleTokenResponseDTO.class
            );



            if (response.getStatusCode() == HttpStatus.OK &&
                    response.getBody() != null &&
                    response.getBody().getAccessToken() != null) {
                log.info("response get Body {}", response.getBody());
                return new SocialTokenValidationResultDTO(TokenStatus.VALID, "유효한 토큰", response.getBody().getAccessToken());
            } else {
                return new SocialTokenValidationResultDTO(TokenStatus.ERROR, "토큰 응답 오류");
            }

        } catch (HttpClientErrorException e) {
            // 예외 처리...
            return new SocialTokenValidationResultDTO(TokenStatus.Failed, "토큰 검증 실패");
        }
    }




}