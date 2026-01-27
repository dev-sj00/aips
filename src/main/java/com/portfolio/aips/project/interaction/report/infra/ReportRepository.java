package com.portfolio.aips.project.interaction.report.infra;

import com.portfolio.aips.project.interaction.report.domain.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {


}
