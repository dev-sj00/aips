package com.portfolio.aips.project.statistics.domain.repo;

import com.portfolio.aips.project.statistics.app.service.command.FindAllStatisticsCommand;
import com.portfolio.aips.project.statistics.app.service.result.FindAllStatisticsResult;

import java.util.List;

public interface ReportStatisticsRepository {
    List<FindAllStatisticsResult> findAllStatisticsByCommand(FindAllStatisticsCommand command);
}
