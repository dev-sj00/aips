package com.portfolio.aips.project.statistics.domain.repo.board_statistics_repository;

import com.portfolio.aips.project.statistics.domain.repo.board_statistics_repository.vo.SaveVO;

import java.util.List;

public interface BoardStatisticsRepository  {
    void save(List<SaveVO> saveVOList);

}
