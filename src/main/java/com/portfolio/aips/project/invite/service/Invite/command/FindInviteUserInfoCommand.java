package com.portfolio.aips.project.invite.service.Invite.command;


public record FindInviteUserInfoCommand(String targetUserName, long ownerUserPk) {
}
