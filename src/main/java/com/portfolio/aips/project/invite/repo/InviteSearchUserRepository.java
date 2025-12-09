package com.portfolio.aips.project.invite.repo;

import com.portfolio.aips.project.users.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteSearchUserRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByNicknameStartingWith(String nickName);
}
