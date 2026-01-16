package com.portfolio.aips.project.interaction.rating.service.popularity_score_calculate.command;

import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;

public record CalculatePopularityScoreCommand(PopularityScoreElementsResult avgRatingInfo, String viewCounts


) {
}
