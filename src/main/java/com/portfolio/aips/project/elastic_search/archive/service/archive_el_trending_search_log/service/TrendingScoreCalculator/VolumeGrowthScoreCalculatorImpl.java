package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator;

import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator.command.CalculateScoreCommand;


//log(current) + current/prev
public class VolumeGrowthScoreCalculatorImpl implements TrendingScoreCalculator {
    @Override
    public double calculateScore(CalculateScoreCommand command) {
        return 0;
    }
}
