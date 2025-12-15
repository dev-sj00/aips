package com.portfolio.aips.project.invite.repo;

import com.portfolio.aips.project.invite.entity.InviteHistoryEntity;
import com.portfolio.aips.project.invite.enums.InviteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InviteHistoryRepository extends JpaRepository<InviteHistoryEntity,Long>, InviteHistoryRepositoryCustom
{

}
