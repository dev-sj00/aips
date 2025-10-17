package com.portfolio.aips.project.config.security.filter.autoLogin.interfaces;

import com.portfolio.aips.project.config.security.filter.autoLogin.dto.DeleteTokenClientInfoDTO;
import com.portfolio.aips.project.config.security.filter.autoLogin.dto.TokenClientAppenderDTO;
import com.portfolio.aips.project.users.repo.RefreshTokenRepository;

import java.io.IOException;

public interface TokenClientAppender {
    void setTokenClientAppender(TokenClientAppenderDTO tokenClientAppenderDTO) throws IOException;
    void deleteTokenClientInfo(DeleteTokenClientInfoDTO DeleteTokenClientInfoDTO) throws IOException;

}
