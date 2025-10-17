package com.portfolio.aips.project.config.security.filter;


import com.portfolio.aips.project.config.security.filter.autoLogin.impl.autoLogin.JwtTokenRotationServiceImpl;
import com.portfolio.aips.project.config.security.filter.autoLogin.interfaces.AutoLoginService;
import com.portfolio.aips.project.exception.CustomException;
import com.portfolio.aips.project.social.provider.dto.SocialTokenValidationResultDTO;
import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.users.dto.TokenPairDTO;
import com.portfolio.aips.project.users.repo.RefreshTokenRepository;

import com.portfolio.aips.project.utils.CookieUtils;
import com.portfolio.aips.project.utils.JwtUtils;
import com.portfolio.aips.project.users.service.CustomUserDetailService;
import com.portfolio.aips.project.social.service.SocialTokenValidatorService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AutoLoginService autoLoginService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {



        log.info("jwtAuthentication Filter 실행 {}", request.getRequestURI());

        if(request.getRequestURI().equals("/.well-known/appspecific/com.chrome.devtools.json")) {
            return;
        }
        autoLoginService.autoLoginProc(request, response);



        filterChain.doFilter(request, response);
    }

















}



