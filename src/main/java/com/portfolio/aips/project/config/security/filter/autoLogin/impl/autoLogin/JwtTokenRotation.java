package com.portfolio.aips.project.config.security.filter.autoLogin.impl.autoLogin;

import com.portfolio.aips.project.config.security.filter.autoLogin.dto.JWTRotationTokenDTO;
import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.users.dto.TokenPairDTO;
import com.portfolio.aips.project.utils.JwtUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;


public class JwtTokenRotation {

    public TokenPairDTO refreshTokenRotation(JWTRotationTokenDTO dto,  RefreshTokenEntity refreshTokenEntity) {


        String refreshToken = dto.getRefreshToken();



        boolean isSameAndNotExpiredRefreshToken = !refreshTokenEntity.isExpired() && refreshTokenEntity.isEquals(refreshToken);
        if (isSameAndNotExpiredRefreshToken) {
            String socialToken = dto.getSocialToken();
            JwtUtils jwtUtils = dto.getJwtUtils();
            return getJwtTokens(refreshToken, socialToken, jwtUtils);
        }
        else {
            throw new RuntimeException("Refresh token expired or invalid");
        }
    }

    private TokenPairDTO getJwtTokens(String refreshToken, String socialToken, JwtUtils jwtUtils) {
        String principalName = jwtUtils.getPrincipalName(refreshToken);
        String provider = jwtUtils.getProvider(refreshToken);


        String newAccessToken = jwtUtils.createJwt(principalName, provider);
        String newRefreshToken = jwtUtils.createJwt(principalName, provider, socialToken);

        return new TokenPairDTO(newAccessToken, newRefreshToken);

    }

}
