package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.rating.repo.result.PopularityScoreElementsResult;
import com.portfolio.aips.project.interaction.rating.service.rating_scheduler.command.FindAllPopularityScoreElementsWithTempTableCommand;

import java.util.List;

public interface RatingBatchRepository {
    List<PopularityScoreElementsResult> FindAllPopularityScoreElementsWithTempTable(FindAllPopularityScoreElementsWithTempTableCommand command) throws InterruptedException;
}

