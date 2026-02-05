package com.portfolio.aips.project.visitor.infra.persistence;

import com.portfolio.aips.project.visitor.domain.entity.VisitorEntity;
import com.portfolio.aips.project.visitor.domain.repo.VisitorRepository;
import com.portfolio.aips.project.visitor.domain.repo.vo.SaveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VisitorJpaRepositoryImpl implements VisitorRepository {

    private final VisitorJpaRepository visitorJpaRepository;



    @Override
    public void save(SaveVO vo) {
        VisitorEntity result  = vo.toEntity();
        visitorJpaRepository.save(result);
    }


}
