package com.portfolio.aips.project.interaction.sanction.infra;

import com.portfolio.aips.project.interaction.sanction.domain.ActiveSanctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ActiveSanctionJpaRepository extends JpaRepository<ActiveSanctionEntity, Long> {

    Optional<ActiveSanctionEntity> findByTargetUserPk(Long targetUserPk);

}
