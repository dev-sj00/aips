package com.portfolio.aips.project.interaction.rating.service.rating_scheduler;


import com.portfolio.aips.project.elastic_search.rating.rating_el_bulk.service.RatingELBulkService;
import com.portfolio.aips.project.elastic_search.rating.rating_el_bulk.service.command.UpdateRatingAndPopularityScoreBulkProcCommand;
import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.rating.repo.RatingBatchRepository;
import com.portfolio.aips.project.interaction.rating.repo.RatingRedisRepository;
import com.portfolio.aips.project.interaction.rating.repo.result.FindAllWithScanResult;
import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.command.CalculatePopularityScoreCommand;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.result.CalculatePopularityScoreResult;
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
    private final RatingELBulkService ratingELBulkService;


    @Override
    @Scheduled(cron = "0 */15 * * * *")
/*    @SchedulerLock(
            name = "updateRatingAndPopularScore",
            lockAtMostFor = "PT30M"
    )*/
    public void updateRatingAndPopularScore() throws InterruptedException {

        List<FindAllWithScanResult> findAllWithScanResults = ratingRedisRepository.findAllWithScan();

        List<PopularityScoreElementsResult> results = ratingBatchRepository.FindAllPopularityScoreElementsWithTempTable(new FindAllPopularityScoreElementsWithTempTableCommand(findAllWithScanResults, 1000));

        //calculate proc

        List<CalculatePopularityScoreResult> popularityResults = new ArrayList<>();

        for(PopularityScoreElementsResult result: results){
            CalculatePopularityScoreResult calculateResult = popularityScoreCalculate.calculatePopularityScore(new CalculatePopularityScoreCommand(result));
            popularityResults.add(calculateResult);
        }


        //색인 bulk update
        ratingELBulkService.updateRatingAndPopularityScoreBulkProc
                (convertCommands(popularityResults));



    }

    private List<UpdateRatingAndPopularityScoreBulkProcCommand> convertCommands(List<CalculatePopularityScoreResult> results)
    {
        return results.stream()
                .map(result -> {
                    PopularityScoreElementsResult info = result.avgRatingInfo();

                    return new UpdateRatingAndPopularityScoreBulkProcCommand(
                            info.boardType(),
                            info.boardPk(),
                            info.usefulnessAvgScore(),
                            info.reliabilityAvgScore(),
                            info.funAvgScore(),
                            info.ratingCount(),
                            result.popularityScore()
                    );
                })
                .toList();
    }




}
