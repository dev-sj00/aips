package com.portfolio.aips.project.interaction.view.repo.dto.result;

import com.portfolio.aips.project.interaction.common.enums.BoardType;

public record FindAllViewBatchProcResult(BoardType boardType, long boardPk, long viewCount) {
}
