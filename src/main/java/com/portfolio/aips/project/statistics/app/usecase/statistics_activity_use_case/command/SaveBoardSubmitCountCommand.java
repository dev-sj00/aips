package com.portfolio.aips.project.statistics.app.usecase.statistics_activity_use_case.command;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.command.FindAllStatisticsCommand;
import com.portfolio.aips.project.statistics.app.router.statistics_usecase.result.FindAllTrendingKeywordStatisticsResult;

import java.util.List;

public record SaveBoardSubmitCountCommand( FindAllStatisticsCommand findAllStatisticsCommand, BoardType boardType) {


    public List<SaveBoardSubmitCountResult.TrendingKeywordItem> toAnotherCommands(List<FindAllTrendingKeywordStatisticsResult.Item> items)
    {
        return items.stream().map(item -> new SaveBoardSubmitCountResult
                .TrendingKeywordItem(item.keyword(), item.docCount(), item.score()))

                .toList();
    }

}
