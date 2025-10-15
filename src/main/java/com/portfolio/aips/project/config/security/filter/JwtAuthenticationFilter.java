package com.portfolio.aips.project.config.security.filter;


import com.portfolio.aips.project.config.security.filter.autoLogin.impl.autoLogin.JwtTokenRotationServiceImpl;
import com.portfolio.aips.project.config.security.filter.autoLogin.interfaces.AutoLoginService;
import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;
import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.users.dto.TokenPairDTO;
import com.portfolio.aips.project.users.repo.RefreshTokenRepository;

import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.users.service.CustomUserDetailService;
import com.portfolio.aips.project.social.service.SocialTokenValidatorService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AutoLoginService autoLoginService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {



        log.info("jwtAuthentication Filter 실행 {}", request.getRequestURI());

        autoLoginService.autoLoginProc(request, response);
        //access token 가져오기
/*        Optional<String> authHeader = Optional.ofNullable(request.getHeader("Authorization"));
        String accessToken = null;



        if(authHeader.isPresent()) {
            accessToken = jwtUtils.extractAccessTokenFromAuthorizationHeader(authHeader.get());
        }


        String refreshToken = cookieUtils.extractCookieToken(request, "refresh_token");
        String deviceId = cookieUtils.extractCookieToken(request, "device_id");


        boolean isNotExpiredRefreshToken = false;

        if(refreshToken != null) {

            isNotExpiredRefreshToken = !jwtUtils.getExpired(refreshToken).before(new Date());
        }

        if (isNotExpiredRefreshToken) {
             boolean isNullOrExpiredAccessToken = accessToken == null || jwtUtils.getExpired(accessToken).before(new Date());

             SocialTokenValidationResultDTO scTokenDTO =  socialTokenValidatorService.validateToken(refreshToken);
             if (isNullOrExpiredAccessToken && scTokenDTO.isValid()) {
                 log.info("token rotation 실행");
                String socialToken = jwtUtils.getSocialToken(refreshToken);
                TokenPairDTO tokenPairDTO = refreshTokenRotation(new RotationTokenDTO(deviceId, refreshToken, socialToken, jwtUtils));
                response.setHeader("Authorization", "Bearer " + tokenPairDTO.getAccessToken());
                setRefreshTokenByClientResponseType(request, response, tokenPairDTO.getRefreshToken());
                createAuthentication(refreshToken);

            }else if(!scTokenDTO.isValid()) {
                 createAuthentication(refreshToken);
            }


        }*/

        filterChain.doFilter(request, response);
    }






/*    private TokenPairDTO refreshTokenRotation(RotationTokenDTO rotationTokenDTO) {

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
    }*/


/*    @Getter
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


    }*/











}



