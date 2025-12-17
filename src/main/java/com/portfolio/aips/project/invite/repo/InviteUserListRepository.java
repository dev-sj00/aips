package com.portfolio.aips.project.invite.repo;

import com.portfolio.aips.project.invite.entity.InviteUserListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteUserListRepository extends JpaRepository<InviteUserListEntity, Long> {

}
