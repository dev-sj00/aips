package com.portfolio.aips.project.config.security.filter.autoLogin.impl.autoLogin;

import com.portfolio.aips.project.config.security.filter.autoLogin.dto.JWTRotationTokenValidVO;
import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.users.entity.RefreshTokenEntity;
import com.portfolio.aips.project.users.dto.TokenPairDTO;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.utils.dto.CreateAcTokenDTO;
import com.portfolio.aips.project.utils.dto.CreateRfTokenDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;


@Slf4j
public class JwtTokenRotation {

    public TokenPairDTO refreshTokenRotation(JWTRotationTokenValidVO vo, RefreshTokenEntity refreshTokenEntity) {


        String refreshToken = vo.getRefreshToken();



        if(refreshTokenEntity.isExpired()) {
            log.info("리프레시 토큰 expired");
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if(!refreshTokenEntity.isEquals(refreshToken))
        {
            log.info("리프레시토큰 안맞음");
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }





        return getJwtTokens(vo);


    }

    private TokenPairDTO getJwtTokens(JWTRotationTokenValidVO vo) {
        JwtUtils jwtUtils = vo.getJwtUtils();
        Long userPk = vo.getUserPk();
        String socialToken = vo.getSocialToken();
        String provider = vo.getProvider();


        Date newIssuedAt = new Date(System.currentTimeMillis());
        String newAccessToken = jwtUtils.createJwt(new CreateAcTokenDTO(userPk, newIssuedAt));
        String newRefreshToken = jwtUtils.createJwt(new CreateRfTokenDTO(userPk, provider, socialToken, newIssuedAt));

        return new TokenPairDTO(newAccessToken, newRefreshToken);

    }

}
