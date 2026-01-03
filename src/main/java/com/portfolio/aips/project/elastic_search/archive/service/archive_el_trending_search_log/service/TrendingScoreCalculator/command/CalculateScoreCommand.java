package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator.command;

public record CalculateScoreCommand(long prevDocCount, long currentDocCount) {
}
