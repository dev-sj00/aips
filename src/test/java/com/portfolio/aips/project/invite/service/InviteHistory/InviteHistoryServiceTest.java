package com.portfolio.aips.project.invite.service.InviteHistory;

import com.portfolio.aips.project.invite.entity.InviteHistoryEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.service.Invite.InviteService;
import com.portfolio.aips.project.invite.service.Invite.command.FindInviteUserInfoCommand;
import com.portfolio.aips.project.invite.service.InviteHistory.command.DeleteHistoryCommand;
import com.portfolio.aips.project.users.entity.UsersEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class InviteHistoryServiceTest {

    @Autowired
    InviteHistoryService inviteHistoryService;

    @Autowired
    InviteService inviteService;

    @Test
    @Transactional
    void test()
    {
        FindInviteUserInfoCommand findCommand = new FindInviteUserInfoCommand("익명", 2L, InviteType.Protect);
        UsersEntity usersEntity = inviteService.findInviteUserInfoProc(findCommand);


        //long invitePk, long targetUserPk, InviteType targetType
       List<InviteHistoryEntity> result = inviteHistoryService.findAllHistory(2L, InviteType.Protect);



       for(InviteHistoryEntity inviteHistoryEntity : result)
       {
           System.out.println(inviteHistoryEntity.getInvitePk());
       }

       inviteHistoryService.deleteHistory(new DeleteHistoryCommand(2L, 29, InviteType.Protect));

    }

}