package com.portfolio.aips.project.interaction.view.repo.dto.request;

import com.portfolio.aips.project.interaction.enums.BoardType;

public record RedisDecreaseViewCountDTO(long boardPk, BoardType boardType) {
}
