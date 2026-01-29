package com.portfolio.aips.project.users.service.user.result;

import com.portfolio.aips.project.users.enums.UserRole;

import java.time.LocalDateTime;

public record FindAllUsersResult(
        Long pk,
        String nickname,
        String principalName,
        String provider,
        UserRole role,
        LocalDateTime createdDateTime
) {
}
