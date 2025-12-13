package com.portfolio.aips.project.invite.repo;

import com.portfolio.aips.project.invite.entity.InvitePolicyEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitePolicyRepository extends JpaRepository<InvitePolicyEntity, Long> {

        //lazy 로딩용
    Optional<InvitePolicyEntity> findByTargetType(InviteType targetType);





}
