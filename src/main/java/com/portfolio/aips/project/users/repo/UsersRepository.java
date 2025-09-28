package com.portfolio.aips.project.users.repo;


import com.portfolio.aips.project.users.domain.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByPrincipalNameAndProvider(String principalName, String provider);
}
