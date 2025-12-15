package com.portfolio.aips.project.invite.service.FavoriteInviteFriend;

import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.invite.repo.FavoriteInviteFriendRepository;
import com.portfolio.aips.project.invite.service.FavoriteInviteFriend.command.AddFavoriteFriendCommand;
import com.portfolio.aips.project.invite.service.Invite.InviteService;
import com.portfolio.aips.project.invite.service.Invite.command.FindInviteUserInfoCommand;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest

class FavoriteInviteFriendServiceTest {

    @Autowired
    FavoriteInviteFriendService favoriteInviteFriendService;


    @Autowired
    InviteService InviteService;
    @Autowired
    private InviteService inviteService;

    @Autowired
    private  EntityManager entityManager;


    @Autowired
    private TransactionTemplate transactionalTemplate;


    @Test
    void addFavoriteFriend_test() {
        AddFavoriteFriendCommand command = new AddFavoriteFriendCommand(2L, 1L, InviteType.Protect);



        // 두 번째 트랜잭션: 같은 엔티티 조회 → 2차 캐시에서 가져오는지 확인
        transactionalTemplate.execute(status -> {
            favoriteInviteFriendService.addFavoriteFriend(command);
            return null; // 반환값 없음
        });
    }
}