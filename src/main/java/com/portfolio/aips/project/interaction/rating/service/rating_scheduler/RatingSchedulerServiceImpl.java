package com.portfolio.aips.project.interaction.rating.service.rating_scheduler;


import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.rating.repo.RatingBatchRepository;
import com.portfolio.aips.project.interaction.rating.repo.RatingRedisRepository;
import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.command.CalculatePopularityScoreCommand;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.service.PopularityScoreCalculate;
import com.portfolio.aips.project.interaction.rating.service.rating_scheduler.command.FindAllPopularityScoreElementsWithTempTableCommand;
import com.portfolio.aips.project.interaction.view.repo.ViewBatchRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingSchedulerServiceImpl implements RatingSchedulerService{

    private final RatingRedisRepository ratingRedisRepository;
    private final RatingBatchRepository ratingBatchRepository;
    private final PopularityScoreCalculate popularityScoreCalculate;


    @Override
    @Scheduled(cron = "0 */15 * * * *")
    @SchedulerLock(
            name = "updateRatingAndPopularScore",
            lockAtMostFor = "PT30M"
    )
    public void updateRatingAndPopularScore() throws InterruptedException {

        List<RatingEntity> entities = ratingRedisRepository.findAllWithScan();

        List<PopularityScoreElementsResult> results = ratingBatchRepository.FindAllPopularityScoreElementsWithTempTable(new FindAllPopularityScoreElementsWithTempTableCommand(entities, 1000, 500));

        //calculate proc

        List<CalculatePopularityScoreCommand> calculateResult = new ArrayList<>();

        for(PopularityScoreElementsResult result: results){
            popularityScoreCalculate.calculatePopularityScore(new CalculatePopularityScoreCommand(result));
        }


        //색인 bulk update



    }



}
