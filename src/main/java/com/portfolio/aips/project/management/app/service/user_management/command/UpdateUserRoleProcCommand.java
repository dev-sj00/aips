package com.portfolio.aips.project.management.app.service.user_management.command;

import com.portfolio.aips.project.users.enums.UserRole;

public record UpdateUserRoleProcCommand(Long userPk, UserRole role) {
}
