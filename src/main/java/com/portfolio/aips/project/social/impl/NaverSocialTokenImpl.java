package com.portfolio.aips.project.social.impl;


import com.portfolio.aips.project.social.provider.SocialTokenProvider;
import com.portfolio.aips.project.social.provider.dto.NaverTokenResponseDTO;
import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;
import com.portfolio.aips.project.social.provider.enums.TokenStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service("naverTokenValidator")
@Slf4j
public class NaverSocialTokenImpl implements SocialTokenProvider {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public SocialTokenValidationResultDTO refreshAccessToken(String refreshToken) {

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


                return new SocialTokenValidationResultDTO(TokenStatus.VALID, "유효한 토큰", response.getBody().getAccessToken());
            } else {
                return new SocialTokenValidationResultDTO(TokenStatus.ERROR, "토큰 응답 오류");
            }

        } catch (HttpClientErrorException e) {
            return new SocialTokenValidationResultDTO(TokenStatus.Failed, "토큰 검증 실패");
        }
    }




}