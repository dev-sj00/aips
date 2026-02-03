package com.portfolio.aips.project.management.app.usecase;

import com.portfolio.aips.project.interaction.report.domain.entity.ReportEntity;
import com.portfolio.aips.project.interaction.report.domain.event.ReportStatusCompletedEvent;
import com.portfolio.aips.project.interaction.report.domain.model.ReportStatus;
import com.portfolio.aips.project.interaction.sanction.app.service.active_sanction.ActiveSanctionCommandService;
import com.portfolio.aips.project.management.app.usecase.command.UpdateReportStatusIfCompletedPublishEventCommand;
import com.portfolio.aips.project.management.domain.repo.ReportManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateReportManagementUseCase {

    private final ReportManagementRepository reportManagementRepository;
    private final ActiveSanctionCommandService activeSanctionCommandService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void execute(UpdateReportStatusIfCompletedPublishEventCommand command) {

        ReportEntity reportEntity = reportManagementRepository.updateReportStatusByReportPk(command.reportPk(), command.reportStatus());

        if (reportEntity.getReportStatus() == ReportStatus.COMPLETED) {
            activeSanctionCommandService.createActiveSanction
                    (reportEntity.getBanType(),
                            reportEntity.getTargetUserPk());
        }

        reportEntity.updateStatusCompleted(); //이벤트 생성

        applicationEventPublisher.publishEvent //이벤트 발행
                (new ReportStatusCompletedEvent
                        (reportEntity.getReason(),
                                reportEntity.getTargetUserPk()));
    }
}
