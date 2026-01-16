package com.portfolio.aips.project.interaction.view.service.view_scheduler;

import com.portfolio.aips.project.elastic_search.view.view_el_bulk.ViewELBulkService;
import com.portfolio.aips.project.elastic_search.view.view_el_bulk.command.UpdateViewCountProcCommand;
import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.repo.ViewBatchRepository;
import com.portfolio.aips.project.interaction.view.repo.ViewRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewSchedulerServiceImpl implements ViewSchedulerService {

    private final ViewBatchRepository viewBatchRepository;
    private final ViewRedisRepository viewRedisRepository;
    private final ViewELBulkService viewELBulkService;

    @Override
    @Scheduled(cron = "0 */1 * * * *")
    @SchedulerLock(
            name = "updateViewCount",
            lockAtMostFor = "PT10M"
    )

    public void updateViewCount() throws IOException, InterruptedException {



        List<ViewEntity> entities = viewRedisRepository.findAllWithScan(); //영속성 없음

        log.info("entities size: {}", entities.size());

        viewBatchRepository.updateViewBatchProc(entities, 50);

        //증분 색인
        viewELBulkService.updateViewCountProc(getUpdateViewCountProcCommand(entities));



    }

    private List<UpdateViewCountProcCommand> getUpdateViewCountProcCommand(List<ViewEntity> entities) {
        List<UpdateViewCountProcCommand> commands = new ArrayList<>();

        for (ViewEntity entity : entities) {
            UpdateViewCountProcCommand command = new UpdateViewCountProcCommand(entity.getBoardPk(), entity.getBoardType(), entity.getViewCount());
            commands.add(command);
        }

        return commands;

    }




}
