package com.portfolio.aips.project.invite.repo;

import com.portfolio.aips.project.invite.entity.InviteEntity;
import com.portfolio.aips.project.invite.entity.InviteUserListEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.users.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<InviteEntity, Long> {
        Optional<InviteEntity> findByOwnerUserPkAndTargetType(long ownerUserPk, InviteType targetType);
}
