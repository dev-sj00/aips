package com.portfolio.aips.project.visitor.app.usecase;

import com.portfolio.aips.project.visitor.domain.repo.VisitorCacheRepository;
import com.portfolio.aips.project.visitor.domain.repo.VisitorRepository;
import com.portfolio.aips.project.visitor.domain.repo.vo.SaveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitorUseCase {

    private final VisitorCacheRepository visitorCacheRepository;
    private final VisitorRepository visitorRepository;


    public void saveVisitorStatistic()
    {
        Long totalCount = visitorCacheRepository.findAll();

        if(totalCount != 0)
        {
            visitorRepository.save(new SaveVO(totalCount));
        }

    }

}
