package com.portfolio.aips.project.url_service.service.url_generator;

import com.portfolio.aips.project.url_service.service.url_generator.enums.URLGeneratorType;

public interface URLGeneratorService {
    String createUrlProc(long userPk, URLGeneratorType urlType);
}
