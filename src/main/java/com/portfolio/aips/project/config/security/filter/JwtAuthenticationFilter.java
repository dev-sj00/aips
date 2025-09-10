package com.portfolio.aips.project.config.security.filter;

import com.portfolio.aips.project.token.validator.enums.TokenStatus;
import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.token.validator.dto.TokenValidationResultDTO;
import com.portfolio.aips.project.users.service.CustomUserDetailService;
import com.portfolio.aips.project.token.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;




@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailService customUserDetailService;
    private final TokenService tokenService;
    private final CookieUtils cookieUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        log.info("JWT AuthenticationFilter request uri: {}", request.getRequestURI());
        String token = cookieUtils.extractCookieToken(request, "access_token");
        Cookie expiredCookie = cookieUtils.getCookie("access_token", null, "/", 0);




        String path = request.getRequestURI();




            if (token == null || !jwtUtils.validateWithClaims(token)) {
                response.addCookie(expiredCookie);
            } else {

                TokenValidationResultDTO resultDTO = validateToken(token); // 쿼리 날림

                log.info("Token Status: {}", resultDTO.getStatus());

                Map<TokenStatus, Runnable> actionMap = Map.of(
                        TokenStatus.VALID, () -> createAuthentication(token),
                        TokenStatus.UPDATE, () -> {
                            String principalName = jwtUtils.getPrincipalName(token);
                            String provider = jwtUtils.getProvider(token);
                            Date expired = jwtUtils.getExpired(token);
                            String updatedToken = jwtUtils.createJwt(principalName, provider, resultDTO.getNewAccessToken(), expired);
                            response.addCookie(cookieUtils.getCookie("access_token", updatedToken, "/", (Integer) jwtUtils.getJWTExpiredTime(Integer.class)));
                            createAuthentication(token); // 여기서 또 날림 1차 캐시 없누
                        }

                );

                actionMap.getOrDefault(resultDTO.getStatus(), () -> {
                    log.error(resultDTO.getMessage());
                    SecurityContextHolder.clearContext();
                    response.addCookie(expiredCookie);
                    throw new RuntimeException(resultDTO.getMessage());
                }).run();

            }





        filterChain.doFilter(request, response);
    }





    private void createAuthentication(String token) {
        String principalName = jwtUtils.getPrincipalName(token);
        String provider = jwtUtils.getProvider(token);
        UserDetails userDetails = customUserDetailService.loadUserByPrincipalNameAndProvider(principalName, provider);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

    }

 private TokenValidationResultDTO validateToken(String token) {


        return tokenService.validateAccessToken(token);

    }


}
