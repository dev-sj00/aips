package com.portfolio.aips.project.config.security.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;
import com.portfolio.aips.project.social.provider.enums.TokenStatus;
import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.users.dto.TokenPairDTO;
import com.portfolio.aips.project.users.repo.RefreshTokenRepository;

import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.users.service.CustomUserDetailService;
import com.portfolio.aips.project.social.service.SocialTokenValidatorService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
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
import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailService customUserDetailService;
    private final SocialTokenValidatorService socialTokenValidatorService;
    private final CookieUtils cookieUtils;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {



        log.info("jwtAuthentication Filter 실행 {}", request.getRequestURI());
        //access token 가져오기
        Optional<String> authHeader = Optional.ofNullable(request.getHeader("Authorization"));
        String accessToken = null;



        if(authHeader.isPresent()) {
            accessToken = jwtUtils.extractAccessTokenFromAuthorizationHeader(authHeader.get());
        }


        String refreshToken = cookieUtils.extractCookieToken(request, "refresh_token");
        String deviceId = cookieUtils.extractCookieToken(request, "device_id");
        String socialToken = cookieUtils.extractCookieToken(request, "social_token");


        boolean isNotExpiredRefreshToken = false;

        if(refreshToken != null) {

            isNotExpiredRefreshToken = !jwtUtils.getExpired(refreshToken).before(new Date());
        }

        if (isNotExpiredRefreshToken) {
             boolean isNullOrExpiredAccessToken = accessToken == null || jwtUtils.getExpired(accessToken).before(new Date());

             SocialTokenValidationResultDTO scTokenDTO =  socialTokenValidatorService.validateToken(refreshToken);
             if (isNullOrExpiredAccessToken && scTokenDTO.isValid()) {
                 log.info("token rotation 실행");
                TokenPairDTO tokenPairDTO = refreshTokenRotation(new RotationTokenDTO(deviceId, refreshToken, socialToken, jwtUtils));
                response.setHeader("Authorization", "Bearer " + tokenPairDTO.getAccessToken());
                setRefreshTokenByClientResponseType(request, response, tokenPairDTO.getRefreshToken());
                createAuthentication(refreshToken);

            }else if(!scTokenDTO.isValid()) {
                 createAuthentication(refreshToken);
            }


        }

        filterChain.doFilter(request, response);
    }


    private void setRefreshTokenByClientResponseType(HttpServletRequest request, HttpServletResponse response, String refreshToken) throws IOException {

        if (isMobileRequest(request)) {

            response.setContentType("application/json;charset=UTF-8");

            Map<String, String> refreshOnly = Map.of("refreshToken", refreshToken);
            new ObjectMapper().writeValue(response.getWriter(), refreshOnly);
        } else {
            // 웹요청 refreshToken은 쿠키로
            Cookie refreshCookie = cookieUtils.getCookie("refresh_token", refreshToken, "/", jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));
            response.addCookie(refreshCookie);
        }

    }


    private boolean isMobileRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String clientType = request.getHeader("X-Client-Type"); // 모바일 앱에서 보낼 커스텀 헤더

        return (clientType != null && clientType.equalsIgnoreCase("MOBILE"))
                || (userAgent != null && userAgent.toLowerCase().contains("mobile"));
    }


    private TokenPairDTO refreshTokenRotation(RotationTokenDTO rotationTokenDTO) {

        Optional<RefreshTokenEntity> refreshTokenEntityOpt = refreshTokenRepository.findByDeviceId(rotationTokenDTO.getDeviceId());
        String refreshToken = rotationTokenDTO.getRefreshToken();
        if (refreshTokenEntityOpt.isEmpty()) {
            throw new RuntimeException("Refresh token entity not found");
        }

        RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();

        boolean isSameAndNotExpiredRefreshToken = !refreshTokenEntity.isExpired() && refreshTokenEntity.isEquals(refreshToken);
        if (isSameAndNotExpiredRefreshToken) {
            return rotationTokenDTO.getJwtTokens();
        }

        throw new RuntimeException("Refresh token expired or invalid");
    }

    @Getter
    private static class RotationTokenDTO {
        String deviceId;
        String refreshToken;
        String socialToken;
        JwtUtils jwtUtils;
        Instant accessTokenExpireTime;
        Instant refreshTokenExpireTime;


        RotationTokenDTO(String deviceId, String refreshToken, String socialToken, JwtUtils jwtUtils) {
            this.deviceId = deviceId;
            this.refreshToken = refreshToken;
            this.socialToken = socialToken;
            this.jwtUtils = jwtUtils;
            this.accessTokenExpireTime = jwtUtils.getJWTExpiredTime("access_token", Instant.class);
            this.refreshTokenExpireTime = jwtUtils.getJWTExpiredTime("refresh_token", Instant.class);
        }

        TokenPairDTO getJwtTokens() {
            String principalName = jwtUtils.getPrincipalName(refreshToken);
            String provider = jwtUtils.getProvider(refreshToken);


            String newAccessToken = jwtUtils.createJwt(principalName, provider, null, jwtUtils.getJWTExpiredTime("access_token", Instant.class));
            String newRefreshToken = jwtUtils.createJwt(principalName, provider, socialToken, jwtUtils.getJWTExpiredTime("refresh_token", Instant.class));

            return new TokenPairDTO(newAccessToken, newRefreshToken);

        }


    }






    private void createAuthentication(String token) {
        String principalName = jwtUtils.getPrincipalName(token);
        String provider = jwtUtils.getProvider(token);
        UserDetails userDetails = customUserDetailService.loadSocialUserByPrincipalNameAndProvider(principalName, provider);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

    }




}



