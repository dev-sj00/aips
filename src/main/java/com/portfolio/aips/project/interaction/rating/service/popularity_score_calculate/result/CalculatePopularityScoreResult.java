package com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.result;

import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;

public record CalculatePopularityScoreResult(PopularityScoreElementsResult avgRatingInfo, Double popularityScore) {
}
