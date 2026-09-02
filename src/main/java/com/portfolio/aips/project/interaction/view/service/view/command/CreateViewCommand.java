package com.portfolio.aips.project.interaction.view.service.view.command;

import com.portfolio.aips.project.interaction.common.enums.BoardType;

public record CreateViewCommand(Long boardPk, BoardType boardType, Long viewCount) {
}
