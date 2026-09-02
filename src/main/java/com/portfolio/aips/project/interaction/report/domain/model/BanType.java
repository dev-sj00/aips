package com.portfolio.aips.project.interaction.report.domain.model;

import java.time.LocalDateTime;

public enum BanType {
        WARN,                       // 게시글 삭제 및 경고
        ARCHIVE_SERVICE_BAN_7D,     // 공개 게시글 작성 불가 7일
        ARCHIVE_SERVICE_BAN_14D,    // 공개 게시글 작성 불가 14일
        LOGIN_BAN_7D,               // 로그인 정지 7일
        LOGIN_BAN_14D,              // 로그인 정지 14일
        LOGIN_BAN_30D,              // 로그인 정지 30일
        PERMANENT_BAN,               // 영구 정지
        REVOKED_BAN;


    public LocalDateTime calculateEndDateTime() {
        LocalDateTime now = LocalDateTime.now();

        if (this == ARCHIVE_SERVICE_BAN_7D) {
            return now.plusDays(7);
        } else if (this == ARCHIVE_SERVICE_BAN_14D) {
            return now.plusDays(14);
        } else if (this == LOGIN_BAN_7D) {
            return now.plusDays(7);
        } else if (this == LOGIN_BAN_14D) {
            return now.plusDays(14);
        } else if (this == LOGIN_BAN_30D) {
            return now.plusDays(30);
        } else if (this == PERMANENT_BAN) {
            return now.plusYears(777);
        }

        throw new IllegalArgumentException("Invalid Ban Type");
    }
}
