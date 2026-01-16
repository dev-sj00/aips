package com.portfolio.aips.project.interaction.view.repo.dto.request;

import com.portfolio.aips.project.interaction.enums.BoardType;

public record ExistsViewCountDTO(BoardType boardType, Long boardPk) {
}
