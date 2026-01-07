package com.portfolio.aips.project.interaction.rating.service.rating_validator.command;

import com.portfolio.aips.project.interaction.rating.enums.BoardType;

public record SaveVerifyCommand(Long boardPk,
                                BoardType boardType) {
}
