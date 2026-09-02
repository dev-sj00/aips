package com.portfolio.aips.project.statistics.app.router.statistics_usecase.command;

import com.portfolio.aips.project.statistics.domain.enums.StatisticsSortType;

public record FindAllStatisticsCommand(int page, int size, StatisticsSortType sortType) {


}
