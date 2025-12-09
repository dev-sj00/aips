package com.portfolio.aips.project.invite.repo;

import com.portfolio.aips.project.invite.entity.FavoriteInviteFriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteInviteFriendRepository extends JpaRepository<FavoriteInviteFriendEntity,Long> {
    int countByInvitePk(Long invitePk);
    void deleteByInvitePkAndUserPk(Long invitePk, Long inviteUserPk);
}
