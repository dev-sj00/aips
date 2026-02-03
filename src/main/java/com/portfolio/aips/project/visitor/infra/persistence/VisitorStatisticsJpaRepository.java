package com.portfolio.aips.project.visitor.infra.persistence;

import com.portfolio.aips.project.visitor.domain.entity.VisitorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorStatisticsJpaRepository extends JpaRepository<VisitorEntity, Long> {
}
