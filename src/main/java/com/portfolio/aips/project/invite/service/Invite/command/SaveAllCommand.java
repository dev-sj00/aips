package com.portfolio.aips.project.invite.service.Invite.command;

import java.util.List;

public record SaveAllCommand(long invitePolicyPk, long ownerUserPk, List<Long> targetUserPkList) {
}
