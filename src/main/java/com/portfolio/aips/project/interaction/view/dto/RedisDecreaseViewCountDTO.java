package com.portfolio.aips.project.interaction.view.dto;

import com.portfolio.aips.project.interaction.enums.BoardType;

public record RedisDecreaseViewCountDTO(long boardPk, BoardType boardType) {
}
