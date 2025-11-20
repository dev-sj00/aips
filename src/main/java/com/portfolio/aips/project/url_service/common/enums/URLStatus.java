package com.portfolio.aips.project.url_service.common.enums;

public enum URLStatus {
    ACTIVE, // 유효함
    SERVER_ERROR, // 서버 오류
    INVALID, //URL이 유효하지않음
    BLOCKED //정책상 차단
}
