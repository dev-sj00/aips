package com.portfolio.aips.project.invite.dto.command;

import com.portfolio.aips.project.invite.enums.InviteType;

public record AddFavoriteFriendCommand(long ownerUserPk, long targetUserPk, InviteType targetType) {
}
