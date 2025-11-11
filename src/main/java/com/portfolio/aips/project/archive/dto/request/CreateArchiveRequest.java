package com.portfolio.aips.project.archive.dto.request;

import com.portfolio.aips.project.config.annotation.archived.CheckArchiveUrl;
import jakarta.validation.constraints.NotBlank;

public record CreateArchiveRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,
        @NotBlank(message = "아카이브 링크는 필수입니다.")
        @CheckArchiveUrl(message = "올바른 아카이브 링크를 입력해주세요.")
        String archiveLink
) {}
