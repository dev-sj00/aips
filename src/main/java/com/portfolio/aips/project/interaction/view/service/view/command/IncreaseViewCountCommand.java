package com.portfolio.aips.project.interaction.view.service.view.command;

import com.portfolio.aips.project.interaction.enums.BoardType;

public record IncreaseViewCountCommand(long boardPk, BoardType boardType, String ViewerUserPk, String ViewerIpAddr) {
}
