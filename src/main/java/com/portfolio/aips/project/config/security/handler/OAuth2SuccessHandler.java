package com.portfolio.aips.project.config.security.handler;

import com.portfolio.aips.project.token.validator.TokenValidator;
import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.users.dto.request.SaveUserTokenRequest;
import com.portfolio.aips.project.users.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Objects;

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
            String refreshToken = Objects.requireNonNull(client.getRefreshToken()).getTokenValue();

            String beanName = provider + "TokenValidator";
            TokenValidator validator = applicationContext.getBean(beanName, TokenValidator.class);

            //로그인 시 새로운 액세스 토큰을 발급받아 다른 환경에서 자동 로그인 해제됨
            String accessToken = validator.validateAndGetAccessToken(refreshToken).getNewAccessToken();




            SaveUserTokenRequest userTokenRequest = new SaveUserTokenRequest(principalName, provider,
                    refreshToken, accessToken, (Instant) jwtUtils.getJWTExpiredTime(Instant.class)
            );

            userService.saveOrUpdateTokenProc(userTokenRequest);

            String jwtToken = jwtUtils.createJwt(principalName, provider, accessToken, (Instant) jwtUtils.getJWTExpiredTime(Instant.class));



            Cookie jwtCookie = cookieUtils.getCookie("access_token", jwtToken, "/", (Integer) jwtUtils.getJWTExpiredTime(Integer.class));

            response.addCookie(jwtCookie);


            response.sendRedirect("/login");

        }


    }
}
