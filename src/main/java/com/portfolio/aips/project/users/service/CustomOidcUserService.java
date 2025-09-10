package com.portfolio.aips.project.users.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;


@Service
@Slf4j
public class CustomOidcUserService extends OidcUserService {



    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {


        try {
            // 카카오 특별 처리
            if ("kakao".equals(oidcUserRequest.getClientRegistration().getRegistrationId())) {
                return this.loadKakaoUser(oidcUserRequest);
            }

            // 기본 OIDC 처리
            OidcUser oidcUser = super.loadUser(oidcUserRequest);
            Set<SimpleGrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new OAuth2AuthenticationException(new OAuth2Error("custom_error"), "사용자 처리 중 오류가 발생했습니다", e);
        }
    }

    private OidcUser loadKakaoUser(OidcUserRequest userRequest) {
        try {
            // 1. 카카오 ID 토큰에서 필요한 정보 추출
            OidcIdToken idToken = userRequest.getIdToken();
            Map<String, Object> attributes = new HashMap<>(idToken.getClaims());

            // 2. 토큰 디버깅
            log.info("ID Token: {}", idToken.getTokenValue());
            log.info("Claims: {}", attributes);

            // 3. sub 클레임이 비어 있는지 확인
            String sub = idToken.getSubject();
            if (sub == null || sub.isEmpty()) {
                // 카카오 ID를 sub로 사용
                // 참고: 카카오 OIDC는 'sub' 대신 다른 필드를 사용할 수 있음
                if (attributes.containsKey("kakao_id")) {
                    sub = attributes.get("kakao_id").toString();
                } else if (attributes.containsKey("id")) {
                    sub = attributes.get("id").toString();
                } else {
                    // 최후의 수단으로 임의의 고유 ID 생성
                    log.info("고유 아이디 생성");
                    sub = UUID.randomUUID().toString();
                }

                // sub 추가
                attributes.put("sub", sub);
                log.info("Sub 클레임 추가: {}", sub);
            }


            // 5. 사용자 정보 객체 생성
            OidcUserInfo userInfo = new OidcUserInfo(attributes);

            // 6. 권한 설정
            Set<SimpleGrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            // 7. DefaultOidcUser 객체 생성 및 반환
            return new DefaultOidcUser(authorities, idToken, userInfo);

        } catch (Exception e) {
            log.error("카카오 사용자 처리 중 오류: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("kakao_user_error"),
                    "카카오 사용자 처리 중 오류가 발생했습니다", e);
        }
    }
}
