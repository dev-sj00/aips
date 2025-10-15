package com.portfolio.aips.project.config.security.filter.autoLogin.interfaces;

import com.portfolio.aips.project.config.security.filter.autoLogin.dto.TokenClientAppenderDTO;

import java.io.IOException;

public interface TokenClientAppender {
    void setTokenClientAppender(TokenClientAppenderDTO tokenClientAppenderDTO) throws IOException;
}
