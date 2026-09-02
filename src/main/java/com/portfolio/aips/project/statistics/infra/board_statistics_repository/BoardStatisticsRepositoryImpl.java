package com.portfolio.aips.project.statistics.infra.board_statistics_repository;

import com.portfolio.aips.project.statistics.domain.entity.BoardStatisticsEntity;
import com.portfolio.aips.project.statistics.domain.repo.board_statistics_repository.BoardStatisticsRepository;
import com.portfolio.aips.project.statistics.domain.repo.board_statistics_repository.vo.SaveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardStatisticsRepositoryImpl implements BoardStatisticsRepository {
    private final BoardStatisticsJpaRepository boardStatisticsJpaRepository;




    @Override
    public void save(List<SaveVO> saveVOList) {


        List<BoardStatisticsEntity> result = saveVOList.stream()
                        .map(s -> new BoardStatisticsEntity(null, s.boardType(), s.submitCount(), null))
                        .toList();

        boardStatisticsJpaRepository.saveAll(result);
    }
}
