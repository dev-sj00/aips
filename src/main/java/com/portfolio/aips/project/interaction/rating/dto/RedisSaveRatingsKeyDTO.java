package com.portfolio.aips.project.interaction.rating.dto;

import com.portfolio.aips.project.interaction.enums.BoardType;

public record RedisSaveRatingsKeyDTO(Long boardPk,
                                     BoardType boardType) {
}
