package com.portfolio.aips.project.users.repo;

import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByUserAgent(String userAgent);
    Optional<RefreshTokenEntity> findByDeviceId(String accessToken);

}
