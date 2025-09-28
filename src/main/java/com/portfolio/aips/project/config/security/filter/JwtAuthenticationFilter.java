package com.portfolio.aips.project.config.security.filter;


import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.users.dto.TokenPairDTO;
import com.portfolio.aips.project.users.repo.RefreshTokenRepository;

import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;
import com.portfolio.aips.project.users.service.CustomUserDetailService;
import com.portfolio.aips.project.social.service.SocialTokenService;
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
import java.util.List;
import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailService customUserDetailService;
    private final SocialTokenService socialTokenService;
    private final CookieUtils cookieUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        //access token 가져오기
        String authHeader = request.getHeader("Authorization");
        String accessToken = extractAccessTokenFromAuthorizationHeader(authHeader);

        String refreshToken = cookieUtils.extractCookieToken(request, "refresh_token");

        Cookie expiredCookie = cookieUtils.getCookie("refresh_token", null, "/", 0);
        String deviceId = cookieUtils.extractCookieToken(request, "device_id");

        if(refreshToken != null) {


            if(accessToken == null || jwtUtils.getExpired(accessToken).before(new Date()))
            {

                TokenPairDTO tokenPairDTO = refreshTokenRotation(accessToken, refreshToken);
                response.setHeader("Authorization", "Bearer " + reissueAccessToken);
            }


        }

        filterChain.doFilter(request, response);
    }

    private TokenPairDTO refreshTokenRotation(String deviceId, String refreshToken)
    {

        Optional<RefreshTokenEntity> refreshTokenEntityOpt = refreshTokenRepository.findByDeviceId(deviceId);
        if(refreshTokenEntityOpt.isEmpty())
        {
            throw new RuntimeException("Refresh token entity not found");
        }

        RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();

        if(!refreshTokenEntity.isExpired() && refreshTokenEntity.isEquals(refreshToken))
        {
            String principalName = jwtUtils.getPrincipalName(refreshToken);
            String provider = jwtUtils.getProvider(refreshToken);


            String newAccessToken = jwtUtils.createJwt(principalName, provider, jwtUtils.getJWTExpiredTime("access_token", Instant.class));
            String newRefreshToken = jwtUtils.createJwt(principalName, provider, jwtUtils.getJWTExpiredTime("refresh_token", Instant.class));

            return new TokenPairDTO(newAccessToken, newRefreshToken);
        }

        throw new RuntimeException("Refresh token expired or invalid");
    }




    private String extractAccessTokenFromAuthorizationHeader(String authHeader)
    {

        if(authHeader.startsWith("Bearer ")){
        String token = authHeader.substring(7);
        log.info("JWT AuthenticationFilter token: {}", token);
        return token;
        }else{
            return null;
        }
    }





    private void createAuthentication(String token) {
        String principalName = jwtUtils.getPrincipalName(token);
        String provider = jwtUtils.getProvider(token);
        UserDetails userDetails = customUserDetailService.loadSocialUserByPrincipalNameAndProvider(principalName, provider);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

    }

 private SocialTokenValidationResultDTO validateToken(String token, HttpServletRequest request) {

        List<String> userInfo =   cookieUtils.extractCookieTokenWithSplitting(request, "userInfo", "_");
        String principalName = userInfo.get(0);
        String provider = userInfo.get(1);

        return socialTokenService.validateAccessToken(token, principalName, provider);

    }


}
