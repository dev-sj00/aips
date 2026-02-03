package com.portfolio.aips.project.visitor.app.usecase;

import com.portfolio.aips.project.visitor.domain.repo.VisitorCacheRepository;
import com.portfolio.aips.project.visitor.domain.repo.VisitorStatisticsRepository;
import com.portfolio.aips.project.visitor.domain.vo.VisitorStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitorUseCase {

    private final VisitorCacheRepository visitorCacheRepository;
    private final VisitorStatisticsRepository visitorStatisticsRepository;


    public void saveVisitorStatistic()
    {
        Long totalCount = visitorCacheRepository.findAll();

        if(totalCount != 0)
        {
            visitorStatisticsRepository.save(new VisitorStatisticsVO(totalCount));
        }

    }

}
