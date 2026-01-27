package com.portfolio.aips.project.interaction.report.app.listener;

import com.portfolio.aips.project.interaction.report.domain.event.ReportStatusCompletedEvent;
import com.portfolio.aips.project.interaction.report.domain.model.BanType;
import com.portfolio.aips.project.interaction.sanction.app.ActiveSanctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReportStatusCompletedEventListener {

    private final ActiveSanctionService activeSanctionService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(ReportStatusCompletedEvent event) {

        BanType banType = event.banType();
        Long targetUserPk = event.targetUserPk();
        
        // 1. 제재 생성
        activeSanctionService.createActiveSanction(banType, targetUserPk);


        // 2. SSE / 알림 (구현해야함)
        // sseNotifier.notifyReportCompleted(reportPk);
    }

}
