package com.portfolio.aips.project.invite.service.Invite;

import com.portfolio.aips.project.invite.service.Invite.command.FindInviteUserInfoCommand;
import com.portfolio.aips.project.invite.service.Invite.command.SaveAllCommand;
import com.portfolio.aips.project.users.entity.UsersEntity;

import java.util.List;

public interface InviteService {
    void saveAll(SaveAllCommand command);
    UsersEntity findInviteUserInfoProc(FindInviteUserInfoCommand command);

}
