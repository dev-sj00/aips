package com.portfolio.aips.project.statistics.app.router.statistics_usecase.result;

import com.portfolio.aips.project.interaction.common.enums.BoardType;

public record findAllSubmitCountsAndBoardType(BoardType boardType, Long submitCount) {
}
