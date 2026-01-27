package com.portfolio.aips.project.interaction.view.repo.dto.request;

import com.portfolio.aips.project.interaction.common.enums.BoardType;

public record FindAllViewBatchProcDTO(BoardType boardType, long boardPk, Long viewCount)  {
}
