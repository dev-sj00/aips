package com.portfolio.aips.project.interaction.report.domain.event;


import com.portfolio.aips.project.interaction.report.domain.model.BanType;

public record ReportStatusCompletedEvent(
        BanType banType,
        Long targetUserPk
) {


}
