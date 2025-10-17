package com.portfolio.aips.project.config.security.filter.autoLogin.impl.autoLogin;

import com.portfolio.aips.project.config.security.filter.autoLogin.dto.JWTRotationTokenVO;
import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.users.dto.TokenPairDTO;
import com.portfolio.aips.project.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JwtTokenRotation {

    public TokenPairDTO refreshTokenRotation(JWTRotationTokenVO dto, RefreshTokenEntity refreshTokenEntity) {


        String refreshToken = dto.getRefreshToken();





        if(refreshTokenEntity.isExpired()) {
            log.info("리프레시 토큰 expired");
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if(!refreshTokenEntity.isEquals(refreshToken))
        {
            log.info("리프레시토큰 안맞음");
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }



        String socialToken = dto.getSocialToken();
        JwtUtils jwtUtils = dto.getJwtUtils();

        return getJwtTokens(refreshToken, socialToken, jwtUtils);


    }

    private TokenPairDTO getJwtTokens(String refreshToken, String socialToken, JwtUtils jwtUtils) {
        String principalName = jwtUtils.getPrincipalName(refreshToken);
        String provider = jwtUtils.getProvider(refreshToken);


        String newAccessToken = jwtUtils.createJwt(principalName, provider);
        String newRefreshToken = jwtUtils.createJwt(principalName, provider, socialToken);

        return new TokenPairDTO(newAccessToken, newRefreshToken);

    }

}
