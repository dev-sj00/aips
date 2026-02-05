package com.portfolio.aips.project.statistics.app.router.statistics_usecase.result;

import java.time.LocalDate;
import java.util.List;


public record FindAllTrendingKeywordStatisticsResult(LocalDate startDate, LocalDate endDate, List<Item> items) {

    public record Item(
            String keyword,
            Long docCount,
            Double score
    ) {}

    public FindAllTrendingKeywordStatisticsResult(LocalDate startDate, List<Item> items)
    {
        this(startDate, startDate.plusDays(6), items);
    }
}
