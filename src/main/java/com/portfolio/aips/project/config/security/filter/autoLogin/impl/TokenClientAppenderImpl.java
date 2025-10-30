package com.portfolio.aips.project.config.security.filter.autoLogin.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.aips.project.config.security.filter.autoLogin.dto.DeleteTokenClientInfoDTO;
import com.portfolio.aips.project.config.security.filter.autoLogin.dto.TokenClientAppenderDTO;
import com.portfolio.aips.project.config.security.filter.autoLogin.interfaces.TokenClientAppender;
import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenClientAppenderImpl implements TokenClientAppender {

    private final CookieUtils cookieUtils;
    private final JwtUtils jwtUtils;
    @Override
    public void setTokenClientAppender(TokenClientAppenderDTO dto) throws IOException {
        String refreshToken = dto.tokenPairDTO().getRefreshToken();
        String  accessToken = dto.tokenPairDTO().getAccessToken();
        if (isMobileRequest(dto.request())) {

            dto.response().setContentType("application/json;charset=UTF-8");

            Map<String, String> refreshOnly = Map.of("refreshToken", refreshToken);
            new ObjectMapper().writeValue(dto.response().getWriter(), refreshOnly);
        } else {
            // 웹요청 refreshToken은 쿠키로
            Cookie refreshCookie = cookieUtils.createCookie("refresh_token", refreshToken, "/", jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));
            dto.response().addCookie(refreshCookie);
            dto.response().setHeader("Access-Control-Expose-Headers", "Authorization");
            dto.response().setHeader("Authorization", "Bearer " + accessToken);


        }
    }


    private boolean isMobileRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String clientType = request.getHeader("X-Client-Type"); // 모바일 앱에서 보낼 커스텀 헤더

        return (clientType != null && clientType.equalsIgnoreCase("MOBILE"))
                || (userAgent != null && userAgent.toLowerCase().contains("mobile"));
    }


    @Override
    public void deleteTokenClientInfo(DeleteTokenClientInfoDTO dto) throws IOException {
        dto.response().setHeader("Authorization", null);

        if (isMobileRequest(dto.request())) {
            dto.response().setContentType("application/json;charset=UTF-8");
            new ObjectMapper().writeValue(dto.response().getWriter(), null);
        }else{
            Cookie delRefreshCookie = cookieUtils.createCookie("refresh_token", dto.refreshToken(), "/", 0);
            Cookie delDeviceId = cookieUtils.createCookie("device_id", dto.refreshToken(), "/", 0);
            dto.response().addCookie(delRefreshCookie);
            dto.response().addCookie(delDeviceId);
            dto.response().setHeader("Authorization", "");
            dto.response().addHeader("Set-Cookie",
                    "test=value; Path=/; HttpOnly; Secure; SameSite=None");
        }
    }



}
