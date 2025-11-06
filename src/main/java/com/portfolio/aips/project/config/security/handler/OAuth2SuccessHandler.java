package com.portfolio.aips.project.config.security.handler;

import com.portfolio.aips.project.social.dto.SaveSocialRefreshTokenInfoRequestDTO;
import com.portfolio.aips.project.users.dto.SaveProcResultDTO;
import com.portfolio.aips.project.users.enums.UserEnvironmentType;
import com.portfolio.aips.project.users.service.CustomUserDetailService;
import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.social.dto.SaveSocialUserInfoRequestDTO;
import com.portfolio.aips.project.users.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    private final JwtUtils jwtUtils;
    private final OAuth2AuthorizedClientService clientService;
    private final CookieUtils cookieUtils;
    private final ApplicationContext applicationContext;


    @Value("${frontend.url}")
    private String frontendUrl;

    private final CustomUserDetailService customUserDetailService;


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
            SaveSocialUserInfoRequestDTO userTokenReq = new SaveSocialUserInfoRequestDTO(principalName, provider, socialRefreshToken);
            String refreshToken = jwtUtils.createJwt(principalName, provider, socialRefreshToken, issuedAt);

            String userAgent = request.getHeader("User-Agent");
            log.info("userAgent: {}", userAgent);

            SaveSocialRefreshTokenInfoRequestDTO refreshTokenReq = getSaveSocialRefreshTokenInfoRequest(deviceId, refreshToken, userAgent);






            SaveProcResultDTO saveResultDTO = userService.saveProc(userTokenReq, refreshTokenReq);


            Instant now = Instant.now();

            Instant expiry = now.plus(1, ChronoUnit.MINUTES); // 로그인 처음할경우 access token

            String accessToken = jwtUtils.createJwt(principalName, provider, issuedAt, Date.from(expiry));

            Cookie refreshTokenCookie;
            Cookie deviceIdCookie;

            if(saveResultDTO.getUserEnvType().equals(UserEnvironmentType.SAME_ENVIRONMENT)) {
                String prevRefreshToken = saveResultDTO.getReusedRefreshTokenResponseDTO().refreshToken();
                String prevDeviceId = saveResultDTO.getReusedRefreshTokenResponseDTO().deviceId();
                refreshTokenCookie = cookieUtils.createCookie("refresh_token", prevRefreshToken, "/",jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));
                deviceIdCookie = cookieUtils.createCookie("device_id", prevDeviceId, "/", jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));

            }else{ // 새로운 환경
                refreshTokenCookie = cookieUtils.createCookie("refresh_token", refreshToken, "/",jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));
                deviceIdCookie = cookieUtils.createCookie("device_id", deviceId, "/", jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));
            }

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
