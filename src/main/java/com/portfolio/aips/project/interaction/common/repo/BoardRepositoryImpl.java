package com.portfolio.aips.project.interaction.common.repo;


import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.url_service.archive.repo.ArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {
    private final ArchiveRepository archiveRepository;

    @Override
    public LocalDateTime findByBoardPkAndBoardTypes(Long boardPk, BoardType boardType) {
        if(BoardType.Archive.equals(boardType))
        {
            return archiveRepository.findByPk(boardPk).getCreatedDateTime();
        }

        throw new IllegalArgumentException("boardType not found");
    }
}
