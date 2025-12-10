package com.portfolio.aips.project.invite.service.Invite.command;

import com.portfolio.aips.project.invite.entity.InviteEntity;
import com.portfolio.aips.project.invite.enums.InviteType;

public record FindInviteUserInfoCommand(String targetUserName, long ownerUserPk, InviteType inviteType) {
}
