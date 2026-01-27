package com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.service;

import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.command.CalculatePopularityScoreCommand;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.result.CalculatePopularityScoreResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        double viewCountsScore = Math.log1p(Double.parseDouble(String.valueOf(ratingInfo.viewCount()))) * 0.3;


        double popularityScore = (sumAvgRatingScore + viewCountsScore) * getHalfLifeWeight(ratingInfo.createdDateTime());

        return new CalculatePopularityScoreResult(command.avgRatingInfo(), popularityScore);


    }

    //반감기 가중치
    private Double getHalfLifeWeight(LocalDateTime createdDateTime) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        LocalDateTime threeYearsAgo = LocalDateTime.now().minusYears(3);


        double weight;
        if (createdDateTime.isAfter(oneMonthAgo)) {
            return weight = 1.0;
        } else if (createdDateTime.isAfter(threeMonthsAgo)) {
            return weight = 0.9;
        } else if (createdDateTime.isAfter(sixMonthsAgo)) {
            return weight = 0.8;
        } else if (createdDateTime.isAfter(oneYearAgo)) {
            return weight = 0.7;
        } else if (createdDateTime.isAfter(threeYearsAgo)) {
            return weight = 0.55;
        } else {
            return weight = 0.4;
        }

    }
}
