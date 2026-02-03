package com.portfolio.aips.project.visitor.infra.schedule;

import com.portfolio.aips.project.visitor.app.usecase.VisitorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorSchedule {


    private final VisitorUseCase visitorUseCase;

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyStatisticsJob()
    {
        visitorUseCase.saveVisitorStatistic();
    }


}
