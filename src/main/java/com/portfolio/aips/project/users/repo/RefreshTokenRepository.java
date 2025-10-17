package com.portfolio.aips.project.users.repo;

import com.portfolio.aips.project.users.domain.RefreshTokenEntity;
import com.portfolio.aips.project.users.domain.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    @Query("SELECT r FROM RefreshTokenEntity r " +
            "WHERE r.usersEntity = :usersEntity " +
            "AND r.userAgent = :userAgent")
    Optional<RefreshTokenEntity> findOneByUsersEntityAndUserAgent(
            @Param("usersEntity") UsersEntity usersEntity,
            @Param("userAgent") String userAgent
    );
    Optional<RefreshTokenEntity> findByDeviceId(String deviceId);




    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.deviceId = :deviceId")
    void deleteByDeviceId(@Param("deviceId") String deviceId);

}
