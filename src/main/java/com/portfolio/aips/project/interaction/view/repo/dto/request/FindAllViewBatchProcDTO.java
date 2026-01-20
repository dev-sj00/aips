package com.portfolio.aips.project.interaction.view.repo.dto.request;

import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.interaction.view.repo.ViewBatchRepository;

public record FindAllViewBatchProcDTO(BoardType boardType, long boardPk, Long viewCount)  {
}
