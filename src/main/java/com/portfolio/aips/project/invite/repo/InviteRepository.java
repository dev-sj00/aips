package com.portfolio.aips.project.invite.repo;

import com.portfolio.aips.project.invite.entity.InviteEntity;
import com.portfolio.aips.project.invite.entity.InviteUserListEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.users.entity.UsersEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<InviteEntity, Long> {

        //lazy 로딩용
        Optional<InviteEntity> findByOwnerUserPkAndTargetType(long ownerUserPk, InviteType targetType);

        @EntityGraph(attributePaths = "favoriteInviteFriends")
        Optional<InviteEntity> findWithFavoritesByOwnerUserPkAndTargetType(Long ownerUserPk, InviteType targetType);

        @EntityGraph(attributePaths = "inviteHistory")
        Optional<InviteEntity> findWithHistoryByOwnerUserPkAndTargetType(Long ownerUserPk, InviteType targetType);
}
