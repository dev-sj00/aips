package com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.service;

import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.command.CalculatePopularityScoreCommand;
import com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.result.CalculatePopularityScoreResult;

import java.util.List;

public interface PopularityScoreCalculate {
    List<CalculatePopularityScoreResult> calculatePopularityScore(CalculatePopularityScoreCommand command);
}
