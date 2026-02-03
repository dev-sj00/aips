package com.portfolio.aips.project.visitor.domain.repo;

import com.portfolio.aips.project.visitor.domain.vo.FindAllVisitorStatisticsResultVO;
import com.portfolio.aips.project.visitor.domain.vo.FindAllVisitorStatisticsVO;
import com.portfolio.aips.project.visitor.domain.vo.VisitorStatisticsVO;
import org.springframework.data.domain.Page;


public interface VisitorStatisticsRepository {
    void save(VisitorStatisticsVO vo);
    Page<FindAllVisitorStatisticsResultVO> findAll(FindAllVisitorStatisticsVO vo);
}
