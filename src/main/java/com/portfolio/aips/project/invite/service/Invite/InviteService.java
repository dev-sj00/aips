package com.portfolio.aips.project.invite.service.Invite;

import com.portfolio.aips.project.users.entity.UsersEntity;

import java.util.List;

public interface InviteService {
    void saveAll(long invitePk, List<Long> invitedUserPkList);
    UsersEntity findInviteUserInfo(String userName);
}
