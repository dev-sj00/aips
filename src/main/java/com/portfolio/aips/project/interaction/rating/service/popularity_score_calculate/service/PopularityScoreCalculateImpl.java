package com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.service;

import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.command.CalculatePopularityScoreCommand;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.result.CalculatePopularityScoreResult;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PopularityScoreCalculateImpl implements PopularityScoreCalculate {

    @Override
    public List<CalculatePopularityScoreResult> calculatePopularityScore(CalculatePopularityScoreCommand command) {
        return List.of();
    }
}
