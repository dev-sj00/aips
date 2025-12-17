package com.portfolio.aips.project.url_service.protect_url.controller;


import com.portfolio.aips.project.url_service.protect_url.dto.request.PasswordCreateRequest;
import com.portfolio.aips.project.url_service.protect_url.service.protect_url.ProtectURLService;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/protect-url")
@RequiredArgsConstructor
public class ProtectURLController {

    private final ProtectURLService protectURLService;

    @PostMapping("/password-create")
    public ResponseEntity<?> createWithPassword(@RequestBody @Valid PasswordCreateRequest request, @AuthenticationPrincipal CustomUserDetails customUserDetail) {
        // 비밀번호 생성 처리 로직

        String slugUrl = protectURLService.createProtectUrl(request, customUserDetail);

        return ResponseEntity.ok().body(slugUrl);
    }



}

