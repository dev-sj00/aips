package com.portfolio.aips.project.config.security.handler;

import com.portfolio.aips.project.social.dto.SaveSocialRefreshTokenInfoRequestDTO;
import com.portfolio.aips.project.users.dto.SaveProcResultDTO;
import com.portfolio.aips.project.users.enums.UserEnvironmentType;
import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.social.dto.SaveSocialUserInfoRequestDTO;
import com.portfolio.aips.project.users.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;



/*
할일 : 리팩토링, AccessToken == RefreshToken (provider, principalName) 만들기
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler  implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final OAuth2AuthorizedClientService clientService;
    private final CookieUtils cookieUtils;
    private final ApplicationContext applicationContext;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


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


            SaveSocialUserInfoRequestDTO userTokenReq = new SaveSocialUserInfoRequestDTO(principalName, provider, socialRefreshToken);
            String refreshToken = jwtUtils.createJwt(principalName, provider, socialRefreshToken);

            String userAgent = request.getHeader("User-Agent");
            log.info("userAgent: {}", userAgent);
            SaveSocialRefreshTokenInfoRequestDTO refreshTokenReq = getSaveSocialRefreshTokenInfoRequest(deviceId, refreshToken, userAgent);




            String accessToken = jwtUtils.createJwt(principalName, provider);

            SaveProcResultDTO saveResultDTO = userService.saveProc(userTokenReq, refreshTokenReq);
            Cookie refreshTokenCookie;
            Cookie deviceIdCookie;

            if(saveResultDTO.getUserEnvType().equals(UserEnvironmentType.SAME_ENVIRONMENT)) {
                String prevRefreshToken = saveResultDTO.getReusedRefreshTokenResponseDTO().refreshToken();
                String prevDeviceId = saveResultDTO.getReusedRefreshTokenResponseDTO().deviceId();
                refreshTokenCookie = cookieUtils.getCookie("refresh_token", prevRefreshToken, "/",jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));
                deviceIdCookie = cookieUtils.getCookie("device_id", prevDeviceId, "/", jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));

            }else{ // 새로운 환경
                refreshTokenCookie = cookieUtils.getCookie("refresh_token", refreshToken, "/",jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));
                deviceIdCookie = cookieUtils.getCookie("device_id", deviceId, "/", jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));
            }




            response.addCookie(refreshTokenCookie);
            response.addCookie(deviceIdCookie);
            response.setHeader("Authorization", "Bearer " + accessToken);
            response.sendRedirect("/");
        }


    }

    private SaveSocialRefreshTokenInfoRequestDTO getSaveSocialRefreshTokenInfoRequest(String deviceId, String refreshToken, String userAgent)
    {
        return new SaveSocialRefreshTokenInfoRequestDTO(
                deviceId,
                refreshToken,
                userAgent,
                jwtUtils.getJWTExpiredTime("refresh_token", Instant.class)
        );
    }
}
