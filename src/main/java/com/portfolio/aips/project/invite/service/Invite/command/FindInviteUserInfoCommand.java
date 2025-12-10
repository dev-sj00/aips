package com.portfolio.aips.project.invite.service.Invite.command;

import com.portfolio.aips.project.invite.enums.InviteType;

public record FindInviteUserInfo(String targetUserName, long ownerUserPk, long invitePk, InviteType targetType) {
}
