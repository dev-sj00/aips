package com.portfolio.aips.project.config.security.filter.autoLogin.dto;

import com.portfolio.aips.project.utils.JwtUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Getter
@Slf4j
public class JWTRotationTokenValidVO {
    private final String refreshToken;
    private final String socialToken;
    private final JwtUtils jwtUtils;
    private final String provider;
    private final Long userPk;

    private final Instant accessTokenExpireTime;
    private final Instant refreshTokenExpireTime;



    public JWTRotationTokenValidVO(String refreshToken, JwtUtils jwtUtils) {
        this.provider = jwtUtils.getProvider(refreshToken);
        this.refreshToken = refreshToken;
        this.socialToken = jwtUtils.getSocialToken(refreshToken);
        this.userPk = jwtUtils.getUserPk(refreshToken);
        this.jwtUtils = jwtUtils;
        this.accessTokenExpireTime = jwtUtils.getJWTExpiredTime("access_token", Instant.class);
        this.refreshTokenExpireTime = jwtUtils.getJWTExpiredTime("refresh_token", Instant.class);
    }







    public boolean needTokenRotation(String accessToken) {

        if (accessToken == null) {
            return true;
        }

        try {
            jwtUtils.getExpired(accessToken);
            log.info("accessToken expired: {}", jwtUtils.getExpired(accessToken));
            return false;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 토큰이 이미 만료됨
            return true;
        } catch (io.jsonwebtoken.JwtException e) {
            // 토큰이 아예 잘못된 경우
            return true;
        }
    }



}
