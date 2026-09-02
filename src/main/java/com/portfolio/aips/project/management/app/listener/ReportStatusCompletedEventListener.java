package com.portfolio.aips.project.management.app.listener;

import com.portfolio.aips.project.interaction.report.domain.event.ReportStatusCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportStatusCompletedEventListener {


    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(ReportStatusCompletedEvent event) {

        String reason = event.reason();
        Long targetUserPk = event.targetUserPk();

        log.info("제제 알림 이벤트 리스너 실행");



        // 2. SSE / 알림 (구현해야함)
        // sseNotifier.notifyReportCompleted(reportPk);
    }

}
