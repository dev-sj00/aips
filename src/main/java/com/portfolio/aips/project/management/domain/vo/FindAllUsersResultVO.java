package com.portfolio.aips.project.management.domain.vo;

import com.portfolio.aips.project.users.enums.UserRole;

import java.time.LocalDateTime;

public record FindAllUsersResultVO(
        Long pk,
        String nickname,
        String principalName,
        String provider,
        UserRole role,
        LocalDateTime createdDateTime
) {
}
