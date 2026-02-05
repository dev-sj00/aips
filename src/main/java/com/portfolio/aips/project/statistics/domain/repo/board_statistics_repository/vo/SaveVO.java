package com.portfolio.aips.project.statistics.domain.repo.board_statistics_repository.vo;

import com.portfolio.aips.project.interaction.common.enums.BoardType;

public record SaveVO(BoardType boardType, Long submitCount) {
}
