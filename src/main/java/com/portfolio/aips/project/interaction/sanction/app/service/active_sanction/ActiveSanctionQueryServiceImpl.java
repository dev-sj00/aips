package com.portfolio.aips.project.interaction.sanction.app.service.active_sanction;

import com.portfolio.aips.project.interaction.sanction.app.service.active_sanction.result.FindActiveSanctionsResult;
import com.portfolio.aips.project.interaction.sanction.infra.ActiveSanctionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActiveSanctionQueryServiceImpl implements ActiveSanctionQueryService {
    private final ActiveSanctionJpaRepository activeSanctionJpaRepository;
    @Override
    public Page<FindActiveSanctionsResult> findAllActiveSanctions(int page, int size) {

        Pageable pageable = PageRequest.of(page, size,  Sort.by(Sort.Direction.DESC, "endDateTime"));

        return  activeSanctionJpaRepository.findAll(pageable).map(FindActiveSanctionsResult::from
        );



    }

    @Override
    public FindActiveSanctionsResult findActiveSanctionsByUserPk(Long userPk) {


        return activeSanctionJpaRepository.findByTargetUserPk(userPk).map(FindActiveSanctionsResult::from)
                .orElseThrow(() -> new RuntimeException("active sanction not found"));


    }
}
