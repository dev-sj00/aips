package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.common.enums.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    RatingEntity findByBoardPkAndBoardTypeAndRaterUserPk(long boardPk, BoardType boardType, long raterUserPk);
}
