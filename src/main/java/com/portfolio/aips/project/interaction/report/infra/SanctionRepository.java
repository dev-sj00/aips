package com.portfolio.aips.project.interaction.report.infra;

import com.portfolio.aips.project.interaction.report.domain.entity.SanctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SanctionRepository extends JpaRepository<SanctionEntity, Long> {


}
