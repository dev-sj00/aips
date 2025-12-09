package com.portfolio.aips.project.invite.dto.command;

import com.portfolio.aips.project.invite.enums.InviteType;

public record AddHistoryCommand(long invitePk, long targetUserPk, InviteType targetType) {
}
