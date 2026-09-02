package com.portfolio.aips.project.url_service.protect_url.repo;

import com.portfolio.aips.project.url_service.protect_url.entity.ProtectURLEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProtectURLRepository extends JpaRepository<ProtectURLEntity, Long> {
    Optional<ProtectURLEntity> findByPkAndUrlPassword(Long pk, String urlPassword);

    @Query("""
    SELECT DISTINCT p FROM protect_url p
    JOIN FETCH p.inviteUserListEntity ivu
    JOIN FETCH ivu.ownerUsersEntity u
    WHERE p.pk = :pk
      AND u.pk = :userPk
    """)
    Optional<ProtectURLEntity> findWithInvitedUser(@Param("pk") long pk, @Param("userPk")long userPk);
}
