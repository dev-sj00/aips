package com.portfolio.aips.project.visitor.infra.persistence;

import com.portfolio.aips.project.visitor.domain.entity.VisitorEntity;
import com.portfolio.aips.project.visitor.domain.repo.VisitorStatisticsRepository;
import com.portfolio.aips.project.visitor.domain.vo.FindAllVisitorStatisticsResultVO;
import com.portfolio.aips.project.visitor.domain.vo.FindAllVisitorStatisticsVO;
import com.portfolio.aips.project.visitor.domain.vo.VisitorStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VisitorStatisticsJpaRepositoryImpl implements VisitorStatisticsRepository {

    private final VisitorStatisticsJpaRepository visitorStatisticsJpaRepository;
    private final VisitorStatisticsQueryDslRepository visitorStatisticsQueryDslRepository;


    @Override
    public void save(VisitorStatisticsVO vo) {
        VisitorEntity result  = vo.toEntity();
        visitorStatisticsJpaRepository.save(result);
    }

    @Override
    public Page<FindAllVisitorStatisticsResultVO> findAll(FindAllVisitorStatisticsVO vo) {

        return visitorStatisticsQueryDslRepository.findAllBySortType(vo);

    }
}
