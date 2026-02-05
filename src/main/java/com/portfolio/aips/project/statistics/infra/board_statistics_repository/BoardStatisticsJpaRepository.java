package com.portfolio.aips.project.statistics.infra.board_statistics_repository;

import com.portfolio.aips.project.statistics.domain.entity.BoardStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardStatisticsJpaRepository extends JpaRepository<BoardStatisticsEntity, Integer> {
}
