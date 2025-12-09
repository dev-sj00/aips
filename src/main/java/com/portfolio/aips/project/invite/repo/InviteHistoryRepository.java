package com.portfolio.aips.project.invite.repo;

import com.portfolio.aips.project.invite.entity.InviteHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteHistoryRepository extends JpaRepository<InviteHistoryEntity,Long>
{
    void deleteByInvitePkAndUserPk(long invitePk, long userPk);
}
