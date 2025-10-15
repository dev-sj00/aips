package com.portfolio.aips.project.config.security.filter.autoLogin.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        if (isMobileRequest(dto.request())) {

            dto.response().setContentType("application/json;charset=UTF-8");

            Map<String, String> refreshOnly = Map.of("refreshToken", dto.refreshToken());
            new ObjectMapper().writeValue(dto.response().getWriter(), refreshOnly);
        } else {
            // 웹요청 refreshToken은 쿠키로
            Cookie refreshCookie = cookieUtils.getCookie("refresh_token", dto.refreshToken(), "/", jwtUtils.getJWTExpiredTime("refresh_token", Integer.class));
            dto.response().addCookie(refreshCookie);
        }
    }

    private boolean isMobileRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String clientType = request.getHeader("X-Client-Type"); // 모바일 앱에서 보낼 커스텀 헤더

        return (clientType != null && clientType.equalsIgnoreCase("MOBILE"))
                || (userAgent != null && userAgent.toLowerCase().contains("mobile"));
    }

}
