package com.portfolio.aips.project.elastic_search.view.view_el_bulk.command;

import com.portfolio.aips.project.interaction.enums.BoardType;

public record UpdateViewCountProcCommand(Long boardPk, BoardType boardType, long viewCount) {
}
