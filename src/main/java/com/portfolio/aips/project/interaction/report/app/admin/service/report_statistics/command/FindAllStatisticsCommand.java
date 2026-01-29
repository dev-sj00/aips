package com.portfolio.aips.project.interaction.report.app.admin.service.report_statistics.command;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import com.portfolio.aips.project.interaction.report.domain.model.ReportDateUnit;

import javax.annotation.Nullable;

public record FindAllStatisticsCommand(ReportDateUnit reportDateUnit, @Nullable BoardType boardType)
{
}
