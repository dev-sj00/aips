package com.portfolio.aips.project.interaction.sanction.app;

import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.sanction.domain.ActiveSanctionEntity;
import com.portfolio.aips.project.interaction.sanction.infra.ActiveSanctionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActiveSanctionServiceImpl implements ActiveSanctionService {
    private final ActiveSanctionJpaRepository activeSanctionJpaRepository;

    @Override
    public void createActiveSanction(BanType banType, Long targetUserPk) {

        if(banType.equals(BanType.WARN))
        {
            return;
        }


        ActiveSanctionEntity entity = ActiveSanctionEntity.builder()
                .startDateTime(LocalDateTime.now())
                .endDateTime(banType.calculateEndDateTime())
                .targetUserPk(targetUserPk)
                .build();

        activeSanctionJpaRepository.save(entity);

    }
}
