package com.portfolio.aips.project.statistics.app.usecase.statistics_activity_use_case.command;

import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.FindAllTrendingKeywordStatisticsResult;

import java.util.Date;
import java.util.List;

public record SaveBoardSubmitCountResult(DateRange dateRange, Long submitCounts, List<TrendingKeywordItem> trendingKeywordItems, Long visitorCount) {

    public record TrendingKeywordItem( String keyword,
                                       Long docCount,
                                       Double score)
    {


    }
}
