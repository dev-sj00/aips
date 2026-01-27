package com.portfolio.aips.project.interaction.rating.repo.enums;

import com.portfolio.aips.project.interaction.common.enums.BoardType;

public enum TargetTableByBoardType {

    archive(BoardType.Archive);

    final BoardType boardType;
    TargetTableByBoardType(BoardType boardType)
    {
        this.boardType = boardType;
    }
}
