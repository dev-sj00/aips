package com.portfolio.aips.project.statistics.app.router.statistics_usecase.command;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportDateUnit;

import javax.annotation.Nullable;

public record FindAllReportStatisticsCommand(ReportDateUnit reportDateUnit, @Nullable BoardType boardType)
{
}
