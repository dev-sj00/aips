package com.portfolio.aips.project.statistics.domain.repo;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.findAllSubmitCountsAndBoardType;

import java.util.List;

public interface BoardCacheRepository {

    void increaseSubmitCountByBoardType(BoardType boardType);
    List<findAllSubmitCountsAndBoardType> findAllSubmitCountsAndBoardType();

}
