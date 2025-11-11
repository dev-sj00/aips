package com.portfolio.aips.project.archive.controller;

import com.portfolio.aips.project.archive.dto.request.CreateArchiveRequest;
import com.portfolio.aips.project.archive.service.archive.ArchiveService;
import com.portfolio.aips.project.users.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/archived")
@Controller
@RequiredArgsConstructor
public class ArchiveRestController {
    private final ArchiveService archiveService;

    @PostMapping("")
    public String createArchivedRequest(@RequestBody @Valid CreateArchiveRequest createArchiveRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        archiveService.createArchive(createArchiveRequest, customUserDetails);


        throw new UnsupportedOperationException("테스트 코드 작성해야함");

    }

}
