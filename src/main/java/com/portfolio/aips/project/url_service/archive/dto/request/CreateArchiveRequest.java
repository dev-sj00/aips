package com.portfolio.aips.project.url_service.archive.dto.request;

import com.portfolio.aips.project.config.annotation.LLMLink.CheckLLMUrl;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateArchiveRequest(
        @NotBlank(message = "URL 링크는 필수입니다.")
        @CheckLLMUrl(message = "올바른 URL 링크를 입력해주세요.")
        String urlLink,


        @NotBlank(message = "제목 입력은 필수입니다.")
        @Size(max = 60, message = "제목은 최대 60자까지 가능합니다.")
        String title,


        @NotBlank
        List<String> tagNames,

        @NotBlank(message = "설명이 필요합니다.")
        @Size(max = 200, message = "설명은 최대 200자까지 가능합니다.")
        String description


) {
}
