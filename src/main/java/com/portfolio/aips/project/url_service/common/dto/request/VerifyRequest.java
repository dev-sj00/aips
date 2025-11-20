package com.portfolio.aips.project.url_service.common.dto.request;

import com.portfolio.aips.project.config.annotation.LLMLink.CheckLLMUrl;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import jakarta.validation.constraints.NotBlank;

public record VerifyRequest(
        @NotBlank(message = "URL 링크는 필수입니다.")
        @CheckLLMUrl(message = "올바른 URL 링크를 입력해주세요.")
        String urlLink,
        @NotBlank(message = "URL 타입은 필수입니다.")
        URLGeneratorType urlGeneratorType

) {}
