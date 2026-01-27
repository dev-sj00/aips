package com.portfolio.aips.project.interaction.rating.service.rating_scheduler.command;

import com.portfolio.aips.project.interaction.rating.repo.result.FindAllWithScanResult;

import java.util.List;

public record FindAllPopularityScoreElementsWithTempTableCommand(List<FindAllWithScanResult> findAllWithScanResults, int batchSize) {
}
