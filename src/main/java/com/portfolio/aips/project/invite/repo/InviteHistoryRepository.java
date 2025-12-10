package com.portfolio.aips.project.invite.repo;

import com.portfolio.aips.project.invite.entity.InviteHistoryEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InviteHistoryRepository extends JpaRepository<InviteHistoryEntity,Long>
{
    @Modifying
    @Query("""
        delete from invite_history ih
        where ih.pk = :historyPk
        and ih.inviteEntity.ownerUserPk = :ownerUserPk
        and ih.inviteEntity.targetType = :targetType
"""
    )


    int deleteHistoryByOwnerPkAndInviteTypeAndHistoryPk(    @Param("ownerUserPk") long ownerUserPk,
                                                            @Param("targetType") InviteType targetType,
                                                            @Param("historyPk") long historyPk);

}
