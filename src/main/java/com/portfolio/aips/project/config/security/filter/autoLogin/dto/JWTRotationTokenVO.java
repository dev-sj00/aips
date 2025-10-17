package com.portfolio.aips.project.config.security.filter.autoLogin.dto;

import com.portfolio.aips.project.utils.JwtUtils;
import lombok.Getter;

import java.time.Instant;
import java.util.Date;

@Getter

public class JWTRotationTokenVO {
    private final String refreshToken;
    private final String socialToken;
    private final JwtUtils jwtUtils;
    private final Instant accessTokenExpireTime;
    private final Instant refreshTokenExpireTime;



    public JWTRotationTokenVO(String refreshToken, String socialToken, JwtUtils jwtUtils) {
        this.refreshToken = refreshToken;
        this.socialToken = socialToken;
        this.jwtUtils = jwtUtils;
        this.accessTokenExpireTime = jwtUtils.getJWTExpiredTime("access_token", Instant.class);
        this.refreshTokenExpireTime = jwtUtils.getJWTExpiredTime("refresh_token", Instant.class);
    }







    public boolean needTokenRotation(String accessToken) {

        return accessToken == null || jwtUtils.getExpired(accessToken).before(new Date());
    }

    public boolean isExpiredRefreshToken(String refreshToken) {
        return jwtUtils.getExpired(refreshToken).before(new Date());
    }




}
