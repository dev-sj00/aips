package com.portfolio.aips.project.config.security.filter.autoLogin.dto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public record TokenClientAppenderDTO(
        HttpServletRequest request,
        HttpServletResponse response,
        String refreshToken

) {
}
