package com.portfolio.aips.project.users.repo;


import com.portfolio.aips.project.users.entity.RefreshTokenEntity;
import com.portfolio.aips.project.users.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByPrincipalNameAndProvider(String principalName, String provider);

    @Query("""
    select u
    from UsersEntity u
    left join fetch u.refreshTokenEntity r
    where u.principalName = :principalName
      and u.provider = :provider
      and (r.userAgent = :userAgent or r is null)
""")
    Optional<UsersEntity> findByPrincipalNameAndProviderAndUserAgent(
            @Param("principalName") String principalName,
            @Param("provider") String provider,
            @Param("userAgent") String userAgent
    );

}
