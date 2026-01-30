package com.portfolio.aips.project.interaction.sanction.app.service.sanction_scheduler;

import com.portfolio.aips.project.interaction.sanction.app.service.active_sanction.ActiveSanctionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SanctionSchedulerServiceImpl implements SanctionSchedulerService {

    private final ActiveSanctionCommandService activeSanctionCommandService;



    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void removeExpiredSanctionScheduler() {

        activeSanctionCommandService.deleteAllExpiredActiveSanction();
    }
}
