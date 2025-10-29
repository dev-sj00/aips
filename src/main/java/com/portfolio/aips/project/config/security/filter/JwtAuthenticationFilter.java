package com.portfolio.aips.project.config.security.filter;


import com.portfolio.aips.project.config.security.filter.autoLogin.interfaces.AutoLoginService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AutoLoginService autoLoginService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {



        log.info("jwtAuthentication Filter 실행 {}", request.getRequestURI());

        if(request.getRequestURI().equals("/.well-known/appspecific/com.chrome.devtools.json") && !request.getRequestURI().startsWith("/api")) {
            return;
        }
        autoLoginService.autoLoginProc(request, response);



        filterChain.doFilter(request, response);
    }

















}



