package com.portfolio.aips.project.interaction.rating.service.rating_scheduler;


import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.rating.repo.RatingBatchRepository;
import com.portfolio.aips.project.interaction.rating.repo.RatingRedisRepository;
import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;
import com.portfolio.aips.project.interaction.rating.service.rating_scheduler.command.FindAllPopularityScoreElementsWithTempTableCommand;
import com.portfolio.aips.project.interaction.view.repo.ViewBatchRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingSchedulerServiceImpl implements RatingSchedulerService{

    private final RatingRedisRepository ratingRedisRepository;
    private final RatingBatchRepository ratingBatchRepository;
    private final ViewBatchRepository viewBatchRepository;

    @Override
    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(
            name = "updateRatingAndPopularScore",
            lockAtMostFor = "PT10M"
    )
    public void updateRatingAndPopularScore() throws InterruptedException {

        List<RatingEntity> entities = ratingRedisRepository.findAllWithScan();

        List<PopularityScoreElementsResult> results = ratingBatchRepository.FindAllPopularityScoreElementsWithTempTable(new FindAllPopularityScoreElementsWithTempTableCommand(entities, 1000, 500));

        //calculate proc

        //색인 bulk update





    }


}
