package com.portfolio.aips.project.statistics.app.usecase.statistics_activity_use_case;

import com.portfolio.aips.project.statistics.app.usecase.statistics_activity_use_case.command.DateRange;
import com.portfolio.aips.project.statistics.app.usecase.statistics_activity_use_case.command.SaveBoardSubmitCountCommand;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.StatisticsFindRouter;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.FindAllBoardStatisticsResult;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.FindAllTrendingKeywordStatisticsResult;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.FindAllVisitorStatisticsResult;
import com.portfolio.aips.project.statistics.app.usecase.statistics_activity_use_case.command.SaveBoardSubmitCountResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsActivityUserCase {

    private final StatisticsFindRouter statisticsFindRouter;


    @Transactional
    public List<SaveBoardSubmitCountResult> execute(SaveBoardSubmitCountCommand command)
    {

        List<FindAllBoardStatisticsResult> submitCountsResults = statisticsFindRouter.execute(command.findAllStatisticsCommand());
        List<FindAllTrendingKeywordStatisticsResult> trendingKeywordsResults = statisticsFindRouter.execute(command.findAllStatisticsCommand());
        List<FindAllVisitorStatisticsResult> visitorsResults = statisticsFindRouter.execute(command.findAllStatisticsCommand());




        //endDate는 무조건 startDate +6임
        Set<DateRange> dateRanges = new TreeSet<>(
                Comparator.comparing(DateRange::startDate)
        );

        HashMap<DateRange, FindAllBoardStatisticsResult> submitCountsResultsMap = new HashMap<>();
        HashMap<DateRange, FindAllTrendingKeywordStatisticsResult> trendingKeywordsResultsMap = new HashMap<>();
        HashMap<DateRange, FindAllVisitorStatisticsResult> visitorsResultsMap = new HashMap<>();



        submitCountsResults.forEach(r -> {
            DateRange dateRange = new DateRange(r.startDate(), r.endDate());
            dateRanges.add(dateRange);
            submitCountsResultsMap.put(dateRange, r);
        });

        trendingKeywordsResults.forEach(r -> {
            DateRange dateRange = new DateRange(r.startDate(), r.endDate());
            dateRanges.add(dateRange);
            trendingKeywordsResultsMap.put(dateRange, r);
        });

        visitorsResults.forEach(r -> {
            DateRange dateRange = new DateRange(r.startDate(), r.endDate());
            dateRanges.add(dateRange);
            visitorsResultsMap.put(dateRange, r);
        });




        List<SaveBoardSubmitCountResult> result  = new ArrayList<>();


        for(DateRange dateRange : dateRanges)
        {
            Long submitCounts =
                    Optional.ofNullable(submitCountsResultsMap.get(dateRange))
                            .map(FindAllBoardStatisticsResult::submitCount)
                            .orElse(0L);

            List<FindAllTrendingKeywordStatisticsResult.Item> items =
                    Optional.ofNullable(trendingKeywordsResultsMap.get(dateRange))
                            .map(FindAllTrendingKeywordStatisticsResult::items)
                            .orElse(List.of());

            Long visitorCount =
                    Optional.ofNullable(visitorsResultsMap.get(dateRange))
                            .map(FindAllVisitorStatisticsResult::totalVisitCount)
                            .orElse(0L);

            result.add(new SaveBoardSubmitCountResult
                    (dateRange,
                    submitCounts,
                    command.toAnotherCommands(items),
                    visitorCount));

        }

        return result;

    }
}
