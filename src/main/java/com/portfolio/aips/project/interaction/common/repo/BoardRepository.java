package com.portfolio.aips.project.interaction.common.repo;


import com.portfolio.aips.project.interaction.common.enums.BoardType;

import java.time.LocalDateTime;

public interface BoardRepository {
    LocalDateTime findByBoardPkAndBoardTypes(Long boardPk, BoardType boardType);
}
