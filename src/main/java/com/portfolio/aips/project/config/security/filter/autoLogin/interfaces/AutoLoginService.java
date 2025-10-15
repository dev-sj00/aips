package com.portfolio.aips.project.config.security.filter.autoLogin.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AutoLoginService {

    void autoLoginProc(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
