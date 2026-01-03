package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator;

import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator.command.CalculateScoreCommand;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


//log(current) + current/prev

@Component
public class VolumeGrowthScoreCalculatorImpl implements TrendingScoreCalculator {
    @Override
    public double calculateScore(CalculateScoreCommand command) {

        return  Math.log(command.currentDocCount()) + ((double) command.currentDocCount() / command.prevDocCount());
    }
}
