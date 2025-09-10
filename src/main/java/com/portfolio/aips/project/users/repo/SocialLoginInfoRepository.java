package com.portfolio.aips.project.users.repo;

import com.portfolio.aips.project.users.domain.SocialLoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialLoginInfoRepository extends JpaRepository<SocialLoginInfo, Long> {
    Optional<SocialLoginInfo> findByPrincipalNameAndProvider(String principalName, String provider);
}
