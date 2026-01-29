package com.portfolio.aips.project.config.security.handler;

import com.portfolio.aips.project.social.dto.SaveSocialRefreshTokenInfoRequestDTO;
import com.portfolio.aips.project.users.dto.RefreshSaveProcResultDTO;
import com.portfolio.aips.project.users.entity.UsersEntity;
import com.portfolio.aips.project.users.enums.UserEnvironmentType;
import com.portfolio.aips.project.users.service.RefreshToken.RefreshTokenService;
import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.social.dto.SaveSocialUserInfoRequestDTO;
import com.portfolio.aips.project.users.service.user.UserService;
import com.portfolio.aips.project.utils.dto.CreateAcTokenDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;



/*
할일 : 리팩토링, AccessToken == RefreshToken (provider, principalName) 만들기
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final OAuth2AuthorizedClientService clientService;
    private final CookieUtils cookieUtils;


    @Value("${frontend.url}")
    private String frontendUrl;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {


        log.info("Success Handler request uri: {}", request.getRequestURI());
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OidcUser oidcUser = (OidcUser) oauth2Token.getPrincipal();


        String principalName = oidcUser.getName();
        String provider = oauth2Token.getAuthorizedClientRegistrationId();

        log.info("provider: {}", provider);
        log.info("principalName: {}", principalName);

        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(provider, oidcUser.getName());


        if(client != null) {
            log.info("실행");



            String deviceId = UUID.randomUUID().toString();

            assert client.getRefreshToken() != null;
            String socialRefreshToken = client.getRefreshToken().getTokenValue();


            Date issuedAt = new Date(System.currentTimeMillis());
            String userAgent = request.getHeader("User-Agent");
            log.info("userAgent: {}", userAgent);

            SaveSocialUserInfoRequestDTO userTokenReq = new SaveSocialUserInfoRequestDTO(principalName, provider, socialRefreshToken, userAgent);
            SaveSocialRefreshTokenInfoRequestDTO refreshTokenReq = getSaveSocialRefreshTokenInfoRequest(deviceId, provider, socialRefreshToken, userAgent);

            UsersEntity usersEntity = userService.saveProc(userTokenReq);
            RefreshSaveProcResultDTO saveResultDTO = refreshTokenService.saveProc(refreshTokenReq, usersEntity);




            String refreshToken = saveResultDTO.getReusedRefreshTokenResponseDTO().refreshToken();
            Cookie refreshTokenCookie =  cookieUtils.createCookie("refresh_token", refreshToken, "/",jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));

            Cookie deviceIdCookie = createDeviceIdCookie(saveResultDTO, deviceId);

            String accessToken = createAccessToken(saveResultDTO, issuedAt.toInstant());


            String redirectUrl = UriComponentsBuilder
                    .fromUriString(frontendUrl+"/auth/success")
                    .queryParam("accessToken", accessToken)
                    .build()
                    .toUriString();

            response.addCookie(refreshTokenCookie);
            response.addCookie(deviceIdCookie);
            response.sendRedirect(redirectUrl);


        }



    }

    private String createAccessToken(RefreshSaveProcResultDTO saveResultDTO, Instant issuedAt) {
        Long userPk = saveResultDTO.getReusedRefreshTokenResponseDTO().userPk();
        Instant expiry = Instant.now().plus(1, ChronoUnit.MINUTES); // 로그인 처음할 경우 1분간 유효

        return jwtUtils.createJwt(
                CreateAcTokenDTO.builder()
                        .userPk(userPk)
                        .issuedAt(Date.from(issuedAt))
                        .build(),
                Date.from(expiry)
        );
    }

    private Cookie createDeviceIdCookie(RefreshSaveProcResultDTO saveResultDTO, String deviceId) {
        String path = "/";
        int maxAge = jwtUtils.getJWTExpiredTime("refresh_token", Integer.class);

        String finalDeviceId;

        if (saveResultDTO.getUserEnvType().equals(UserEnvironmentType.SAME_ENVIRONMENT)) {
            finalDeviceId = saveResultDTO.getReusedRefreshTokenResponseDTO().deviceId();
        } else {
            finalDeviceId = deviceId;
        }

        return cookieUtils.createCookie("device_id", finalDeviceId, path, maxAge);
    }

    private SaveSocialRefreshTokenInfoRequestDTO getSaveSocialRefreshTokenInfoRequest(String deviceId, String provider, String socialRefreshToken, String userAgent)
    {
        return new SaveSocialRefreshTokenInfoRequestDTO(
                deviceId,
                provider,
                socialRefreshToken,
                userAgent,
                jwtUtils.getJWTExpiredTime("refresh_token", Instant.class)
        );
    }
}
