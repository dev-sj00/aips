package com.portfolio.aips.project.interaction.rating.service.rating_scheduler.command;

import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;

import java.util.List;

public record FindAllPopularityScoreElementsWithTempTableCommand(List<RatingEntity> ratingEntities, int batchSize, int tempBatchSize) {
}
