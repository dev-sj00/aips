package com.portfolio.aips.project.interaction.rating.repo;

import com.portfolio.aips.project.interaction.rating.entity.RatingEntity;
import com.portfolio.aips.project.interaction.rating.repo.result.BoardAvgRatingScoreResult;

import java.util.List;

public interface RatingBatchRepository {
    List<BoardAvgRatingScoreResult> findAllAvgRatingScoresWithTempTable(List<RatingEntity> batch, int batchSize);
}
