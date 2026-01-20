package com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.service;

import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.command.CalculatePopularityScoreCommand;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.result.CalculatePopularityScoreResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PopularityScoreCalculateImpl implements PopularityScoreCalculate {

    @Override
    public CalculatePopularityScoreResult calculatePopularityScore(CalculatePopularityScoreCommand command) {

        PopularityScoreElementsResult ratingInfo = command.avgRatingInfo();

        double sumAvgRatingScore = ((ratingInfo.funAvgScore()
                                    + ratingInfo.reliabilityAvgScore()
                                    + ratingInfo.usefulnessAvgScore()) / 3
                                    + Math.log1p(ratingInfo.ratingCount())) * 0.5;

        double viewCountsScore = Math.log1p(Double.parseDouble(String.valueOf(ratingInfo.ratingCount()))) * 0.3;


        return new CalculatePopularityScoreResult(command.avgRatingInfo(), sumAvgRatingScore + viewCountsScore);


    }
}
