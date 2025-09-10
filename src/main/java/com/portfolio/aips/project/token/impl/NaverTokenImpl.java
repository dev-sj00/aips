package com.portfolio.aips.project.token.impl;


import com.portfolio.aips.project.token.validator.TokenValidator;
import com.portfolio.aips.project.token.validator.dto.NaverTokenResponseDTO;
import com.portfolio.aips.project.token.validator.dto.TokenValidationResultDTO;
import com.portfolio.aips.project.token.validator.enums.TokenStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service("naverTokenValidator")
public class NaverTokenImpl implements TokenValidator{

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public TokenValidationResultDTO validateAndGetAccessToken(String refreshToken) {

        try {

            String url = "https://nid.naver.com/oauth2.0/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", naverClientId);
            params.add("client_secret", naverClientSecret);
            params.add("refresh_token", refreshToken);
            params.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<NaverTokenResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, NaverTokenResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK &&
                    response.getBody() != null &&
                    response.getBody().getAccessToken() != null) {

                return new TokenValidationResultDTO(TokenStatus.VALID, "유효한 토큰", response.getBody().getAccessToken());
            } else {
                return new TokenValidationResultDTO(TokenStatus.ERROR, "토큰 응답 오류");
            }

        } catch (HttpClientErrorException e) {
            return new TokenValidationResultDTO(TokenStatus.Failed, "토큰 검증 실패");
        }
    }


}