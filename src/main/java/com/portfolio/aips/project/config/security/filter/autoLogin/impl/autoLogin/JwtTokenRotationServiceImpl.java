package com.portfolio.aips.project.config.security.filter.autoLogin.impl.autoLogin;

import com.portfolio.aips.project.config.security.filter.autoLogin.dto.DeleteTokenClientInfoDTO;
import com.portfolio.aips.project.config.security.filter.autoLogin.dto.JWTRotationTokenVO;
import com.portfolio.aips.project.config.security.filter.autoLogin.dto.TokenClientAppenderDTO;
import com.portfolio.aips.project.config.security.filter.autoLogin.interfaces.AutoLoginService;
import com.portfolio.aips.project.config.security.filter.autoLogin.interfaces.TokenClientAppender;
import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.exception.ErrorCode;
import com.portfolio.aips.project.social.service.SocialTokenValidator;
import com.portfolio.aips.project.users.entity.RefreshTokenEntity;
import com.portfolio.aips.project.users.dto.TokenPairDTO;
import com.portfolio.aips.project.users.repo.RefreshTokenRepository;
import com.portfolio.aips.project.users.service.CustomUserDetailService;
import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenRotationServiceImpl extends JwtTokenRotation implements AutoLoginService {
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final TokenClientAppender tokenClientAppender;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SocialTokenValidator socialTokenValidator;
    private final EntityManager entityManager;
    private final CustomUserDetailService customUserDetailService;


    @Override
    @Transactional(noRollbackFor = CustomException.class)
    public void autoLoginProc(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> authHeader = Optional.ofNullable(request.getHeader("Authorization"));
        String accessToken = null;





        if(authHeader.isPresent()) {
            accessToken = jwtUtils.extractAccessTokenFromAuthorizationHeader(authHeader.get());

        }



        String refreshToken = cookieUtils.extractCookieToken(request, "refresh_token");
        String deviceId = cookieUtils.extractCookieToken(request, "device_id");



        if(refreshToken == null || deviceId == null) {log.info("323232"); return;}



        try {
            String socialToken = jwtUtils.getSocialToken(refreshToken);
            JWTRotationTokenVO jwtRotationTokenVO = new JWTRotationTokenVO(refreshToken, socialToken, jwtUtils);
            String newAccessToken = null;

            if (jwtRotationTokenVO.needTokenRotation(accessToken)) {

                log.info("access token has been rotated");
                socialTokenValidator.validateToken(refreshToken);
                newAccessToken = tokenRotationProc(deviceId, jwtRotationTokenVO, request, response);
            }

            createAuthentication(newAccessToken != null ? newAccessToken : accessToken);

        }catch (CustomException e) {
            log.info(e.getMessage());
            refreshTokenRepository.deleteByDeviceId(deviceId);
            entityManager.flush();
            tokenClientAppender.deleteTokenClientInfo(new DeleteTokenClientInfoDTO(request, response, refreshToken, accessToken));
            throw new CustomException(e.getErrorCode());
        }

    }









    private String tokenRotationProc(String deviceId, JWTRotationTokenVO dto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<RefreshTokenEntity> refreshTokenEntityOpt = refreshTokenRepository.findByDeviceId(deviceId);
        RefreshTokenEntity refreshTokenEntity =  refreshTokenEntityOpt.orElseThrow(() -> new CustomException(ErrorCode.DEVICE_NOT_FOUND));
        TokenPairDTO tokenPairDTO = refreshTokenRotation(dto, refreshTokenEntity);

        tokenClientAppender.setTokenClientAppender(getTokenClientAppender(request, response, tokenPairDTO));

        refreshTokenEntity.setRefreshToken(tokenPairDTO.getRefreshToken());
        refreshTokenEntity.setExpiresAt(jwtUtils.getJWTExpiredTime("refresh_token", Instant.class));

        return tokenPairDTO.getAccessToken();
    }


    private TokenClientAppenderDTO getTokenClientAppender(    HttpServletRequest request,
                                                           HttpServletResponse response,
                                                           TokenPairDTO tokenPairDTO) {
        return new TokenClientAppenderDTO(request, response, tokenPairDTO);
    }


    private void createAuthentication(String token)
    {
        String principalName = jwtUtils.getPrincipalName(token);
        String provider = jwtUtils.getProvider(token);

        UserDetails userDetails = customUserDetailService.loadSocialUserByPrincipalNameAndProvider(principalName, provider);

        // ✅ 4️⃣ Authentication 객체 생성
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        // ✅ 5️⃣ SecurityContext에 Authentication 세팅

        log.info("인증 객체 생성");
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }



}
