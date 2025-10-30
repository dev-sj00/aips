package com.portfolio.aips.project.config.security.filter.autoLogin.dto;

import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.utils.JwtUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Date;

@Getter
@Slf4j
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

    public boolean isExpiredRefreshToken() {
        return jwtUtils.getExpired(this.refreshToken).before(new Date());
    }

    public void isDifferentFromRefreshToken(String accessToken) {

        if(accessToken == null)
        {
            return;
        }

        String acPrincipalName = jwtUtils.getPrincipalName(accessToken);
        String acProvider = jwtUtils.getProvider(accessToken);
        Date acIssuedAt = jwtUtils.getIssuedAt(accessToken);
        String rfPrincipalName = jwtUtils.getPrincipalName(refreshToken);
        String rfProvider = jwtUtils.getProvider(refreshToken);
        Date rfIssuedAt = jwtUtils.getIssuedAt(refreshToken);



        if(!(acPrincipalName.equals(rfPrincipalName) && acProvider.equals(rfProvider) && acIssuedAt.equals(rfIssuedAt)))
        {
            log.error("token pair is not validate");
            throw new CustomException(ErrorCode.TOKEN_PAIR_MISMATCH);
        }
    }

}
