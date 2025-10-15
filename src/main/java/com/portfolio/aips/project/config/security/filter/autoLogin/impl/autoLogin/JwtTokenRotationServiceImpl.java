package com.portfolio.aips.project.config.security.filter.autoLogin.impl.autoLogin;

import com.portfolio.aips.project.config.security.filter.autoLogin.dto.JWTRotationTokenDTO;
import com.portfolio.aips.project.config.security.filter.autoLogin.dto.TokenClientAppenderDTO;
import com.portfolio.aips.project.config.security.filter.autoLogin.interfaces.AutoLoginService;
import com.portfolio.aips.project.config.security.filter.autoLogin.interfaces.TokenClientAppender;
import com.portfolio.aips.project.social.service.SocialTokenValidatorService;
import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.users.dto.TokenPairDTO;
import com.portfolio.aips.project.users.repo.RefreshTokenRepository;
import com.portfolio.aips.project.users.service.CustomUserDetailService;
import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenRotationServiceImpl extends JwtTokenRotation implements AutoLoginService {
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final TokenClientAppender tokenClientAppender;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SocialTokenValidatorService socialTokenValidatorService;
    private final CustomUserDetailService customUserDetailService;

    @Override
    public void autoLoginProc(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> authHeader = Optional.ofNullable(request.getHeader("Authorization"));
        String accessToken = null;



        if(authHeader.isPresent()) {
            accessToken = jwtUtils.extractAccessTokenFromAuthorizationHeader(authHeader.get());
        }



        String refreshToken = cookieUtils.extractCookieToken(request, "refresh_token");
        String deviceId = cookieUtils.extractCookieToken(request, "device_id");

        if(refreshToken == null || deviceId == null) return;

        String socialToken = jwtUtils.getSocialToken(refreshToken);

        //두개 null pointer


        JWTRotationTokenDTO jwtRotationTokenDTO = new JWTRotationTokenDTO(refreshToken, socialToken, jwtUtils);


        if (jwtRotationTokenDTO.isExpiredRefreshToken(refreshToken)) return;

        boolean isSocialTokenValid = socialTokenValidatorService.validateToken(refreshToken).isValid();
        // null 포인터

        if(!isSocialTokenValid) return;


        if (jwtRotationTokenDTO.needTokenRotation(accessToken)) {
            log.info("Social token has been rotated");
            tokenRotationProc(deviceId, jwtRotationTokenDTO, request, response);
        }

        createAuthentication(refreshToken);
    }



    private void tokenRotationProc(String deviceId, JWTRotationTokenDTO dto,  HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<RefreshTokenEntity> refreshTokenEntityOpt = refreshTokenRepository.findByDeviceId(deviceId);
        String refreshToken = dto.getRefreshToken();

        if(refreshTokenEntityOpt.isPresent()) {
            RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();
            TokenPairDTO tokenPairDTO = refreshTokenRotation(dto, refreshTokenEntity);
            response.setHeader("Authorization", "Bearer " + tokenPairDTO.getAccessToken());
            tokenClientAppender.setTokenClientAppender(getTokenClientAppender(request, response, refreshToken));
            createAuthentication(refreshToken);
        }else{
            throw new RuntimeException("Refresh token entity not found");
        }
    }


    private TokenClientAppenderDTO getTokenClientAppender(    HttpServletRequest request,
                                                           HttpServletResponse response,
                                                           String refreshToken) {
        return new TokenClientAppenderDTO(request, response, refreshToken);
    }


    private void createAuthentication(String token) {
        String principalName = jwtUtils.getPrincipalName(token);
        String provider = jwtUtils.getProvider(token);
        UserDetails userDetails = customUserDetailService.loadSocialUserByPrincipalNameAndProvider(principalName, provider);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

    }
}
