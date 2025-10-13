package com.portfolio.aips.project.social.provider.impl;


import com.portfolio.aips.project.social.provider.SocialTokenProvider;
import com.portfolio.aips.project.social.provider.dto.KakaoTokenResponseDTO;
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

@Service("kakaoTokenValidator")
@Slf4j
public class KakaoSocialTokenImpl implements SocialTokenProvider {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    // 카카오는 client-secret이 없으므로 제거

    private final RestTemplate restTemplate = new RestTemplate();

    public SocialTokenValidationResultDTO refreshAccessToken(String refreshToken) {

        try {

            String url = "https://kauth.kakao.com/oauth/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", kakaoClientId);
            params.add("refresh_token", refreshToken);
            params.add("grant_type", "refresh_token");
            // 카카오는 client_secret 파라미터 없음

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<KakaoTokenResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, KakaoTokenResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK &&
                    response.getBody() != null &&
                    response.getBody().getAccessToken() != null) {
                log.info("유효한 토큰");
                return new SocialTokenValidationResultDTO(TokenStatus.VALID, "유효한 토큰", response.getBody().getAccessToken());
            } else {
                return new SocialTokenValidationResultDTO(TokenStatus.ERROR, "토큰 응답 오류");
            }

        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            return new SocialTokenValidationResultDTO(TokenStatus.Failed, "토큰 검증 실패");
        }
    }




}
