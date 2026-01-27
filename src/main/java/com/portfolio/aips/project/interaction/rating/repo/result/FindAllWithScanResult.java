package com.portfolio.aips.project.interaction.rating.repo.result;

import com.portfolio.aips.project.interaction.common.enums.BoardType;

import java.time.LocalDateTime;

public record FindAllWithScanResult(Long boardPk, BoardType boardType, LocalDateTime createdDateTime) {
}
