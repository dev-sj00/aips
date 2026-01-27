package com.portfolio.aips.project.interaction.rating.service.rating.command;

import com.portfolio.aips.project.interaction.common.enums.BoardType;

public record FindOwnRatings(Long boardPk,
                             BoardType boardType, Long ownUserPk) {
}
