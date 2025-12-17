package com.portfolio.aips.project.invite.service.invitePolicy;

import com.portfolio.aips.project.invite.entity.InvitePolicyEntity;
import com.portfolio.aips.project.invite.enums.InviteType;

public interface InvitePolicyService {
    InvitePolicyEntity findInvitePolicyByInviteType(InviteType inviteType);
}
