package com.portfolio.aips.project.interaction.view.repo;

import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewRepository extends JpaRepository<ViewEntity, Long> {
    ViewEntity findByBoardPkAndBoardType(Long boardPk, BoardType boardType);
}
