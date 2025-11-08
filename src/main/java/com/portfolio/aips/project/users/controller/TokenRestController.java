package com.portfolio.aips.project.users.controller;


import com.portfolio.aips.project.users.dto.CustomUserDetails;
import com.portfolio.aips.project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//페이지 이동할때마다 jwt filter 인증용
@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenRestController {

    private final JwtUtils jwtUtils;


    @GetMapping("/api/test")
    public void Test(@AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("userDetails={}", userDetails);
        log.info("username={}", userDetails.getUsername());
        log.info("userPk={}", userDetails.getPk());
    }


}


