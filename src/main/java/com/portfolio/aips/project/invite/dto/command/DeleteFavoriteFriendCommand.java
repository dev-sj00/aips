package com.portfolio.aips.project.invite.dto.command;

import com.portfolio.aips.project.invite.enums.InviteType;

public record DeleteFavoriteFriendCommand(long ownerUserPk, long targetUserPk, InviteType targetType) {
}
