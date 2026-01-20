package com.portfolio.aips.project.interaction.view.service.view_scheduler;

import com.portfolio.aips.project.elastic_search.view.view_el_bulk.ViewELBulkService;
import com.portfolio.aips.project.elastic_search.view.view_el_bulk.command.UpdateViewCountProcCommand;
import com.portfolio.aips.project.interaction.view.entity.ViewEntity;
import com.portfolio.aips.project.interaction.view.repo.ViewBatchRepository;
import com.portfolio.aips.project.interaction.view.repo.ViewRedisRepository;
import com.portfolio.aips.project.interaction.view.repo.dto.request.RedisDecreaseViewCountDTO;
import com.portfolio.aips.project.interaction.view.repo.dto.result.FindAllViewBatchProcResult;
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

        viewBatchRepository.updateViewBatchProc(entities, 300);

        List<FindAllViewBatchProcResult> findViewResults = viewBatchRepository.findAllViewBatchProc(entities, 1000);

        findViewResults.forEach(viewBatch -> {
            log.info("view batch: {}", viewBatch);
        });

        //증분 색인
        viewELBulkService.updateViewCountProc(getUpdateViewCountProcCommand(findViewResults));

        findViewResults.forEach(viewEntity -> {
            log.info("view entity: {}", viewEntity);
           viewRedisRepository.decreaseViewCount(new RedisDecreaseViewCountDTO(viewEntity.boardPk(), viewEntity.boardType()));
        });



    }

    private List<UpdateViewCountProcCommand> getUpdateViewCountProcCommand(List<FindAllViewBatchProcResult> entities) {
        List<UpdateViewCountProcCommand> commands = new ArrayList<>();

        for (FindAllViewBatchProcResult entity : entities) {
            UpdateViewCountProcCommand command = new UpdateViewCountProcCommand(entity.boardPk(), entity.boardType(), entity.viewCount());
            commands.add(command);
        }

        return commands;

    }




}
