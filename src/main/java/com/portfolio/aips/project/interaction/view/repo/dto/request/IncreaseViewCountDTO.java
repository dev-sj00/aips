package com.portfolio.aips.project.interaction.view.repo.dto.request;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import org.springframework.lang.Nullable;

public record IncreaseViewCountDTO(long boardPk, BoardType boardType, @Nullable String ViewerUserPk, @Nullable String ViewerIpAddr) {
}
