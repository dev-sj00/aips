package com.portfolio.aips.project.interaction.sanction.app.service.active_sanction;


import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.sanction.app.service.active_sanction.command.UpdateActiveSanctionCommand;

import com.portfolio.aips.project.interaction.sanction.domain.ActiveSanctionEntity;
import com.portfolio.aips.project.interaction.sanction.domain.QActiveSanctionEntity;
import com.portfolio.aips.project.interaction.sanction.infra.ActiveSanctionJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActiveSanctionCommandServiceImpl implements ActiveSanctionCommandService {
    private final ActiveSanctionJpaRepository activeSanctionJpaRepository;
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;



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


    @Override
    @Transactional
    public void updateActiveSanction(UpdateActiveSanctionCommand command) {

        ActiveSanctionEntity entity  = activeSanctionJpaRepository.findByTargetUserPk(command.targetUserPk())
                .orElseThrow(() -> new RuntimeException("active sanction not found"));

        LocalDateTime endDateTime = command.banType().calculateEndDateTime();

        entity.updateEndDateTime(endDateTime);





    }

    @Override
    @Transactional
    public void deleteAllExpiredActiveSanction() {
        QActiveSanctionEntity entity = QActiveSanctionEntity.activeSanctionEntity;

        LocalDateTime now = LocalDateTime.now();

        queryFactory.delete(entity).where(entity.endDateTime.before(now)).execute();
        em.flush();
    }
}
