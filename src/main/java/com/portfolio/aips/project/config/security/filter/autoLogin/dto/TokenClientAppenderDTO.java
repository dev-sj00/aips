package com.portfolio.aips.project.config.security.filter.autoLogin.dto;

import com.portfolio.aips.project.users.dto.TokenPairDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public record TokenClientAppenderDTO(
        HttpServletRequest request,
        HttpServletResponse response,
        TokenPairDTO tokenPairDTO

) {
}
