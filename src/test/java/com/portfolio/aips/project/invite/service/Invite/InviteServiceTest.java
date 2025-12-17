package com.portfolio.aips.project.invite.service.Invite;

import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.service.Invite.command.FindInviteUserInfoCommand;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;


@SpringBootTest

class InviteServiceTest {

    @Autowired
    private InviteService inviteService;

    @Autowired
    private EntityManager entityManager;


    @Autowired
    private TransactionTemplate transactionalTemplate;


    @Test

    public void searchUserInfoProc_test()
    {



        FindInviteUserInfoCommand command = new FindInviteUserInfoCommand("익명", 1L);


        for(int i = 0; i < 2; i++) {
            System.out.println(i+"번 실행");
            transactionalTemplate.execute(status -> {
                inviteService.findInviteUserInfoProc(command);
                entityManager.flush();
                return null; // 반환값 없음
            });
        }








    }


}