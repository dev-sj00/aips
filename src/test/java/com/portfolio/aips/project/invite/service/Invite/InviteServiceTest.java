package com.portfolio.aips.project.invite.service.Invite;

import com.portfolio.aips.project.invite.entity.InviteEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.service.Invite.command.FindInviteUserInfoCommand;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest

class InviteServiceTest {

    @Autowired
    private InviteService inviteService;

    @Autowired
    private EntityManager entityManager;


    @Test
    @Transactional
    public void searchUserInfoProc_test()
    {



        FindInviteUserInfoCommand command = new FindInviteUserInfoCommand("익명", 2L, InviteType.Protect);

        for(int i=0; i<6; i++) {
            inviteService.findInviteUserInfoProc(command);
            entityManager.flush();
        }




    }


}