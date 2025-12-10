package com.portfolio.aips.project.invite.service.InviteHistory.command;

import com.portfolio.aips.project.invite.enums.InviteType;

public record DeleteHistoryCommand(long ownerUserPk, long targetUserPk, InviteType targetType) {
}
