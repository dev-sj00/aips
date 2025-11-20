package com.portfolio.aips.project.url_service.common.dto.commnad;

import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;

public record CreateURLStatusCommand(String urlLink, URLGeneratorType urlType) {
}


