package com.portfolio.aips.project.interaction.report.domain.model;

import java.time.LocalDateTime;

public enum ReportDateUnit {
    YEAR,
    MONTH,
    WEEK;



    public LocalDateTime toLocalDateTime()
    {
        if(this.equals(ReportDateUnit.YEAR)) {
            return LocalDateTime.now().minusYears(1);
        }else if(this.equals(ReportDateUnit.MONTH)) {
            return LocalDateTime.now().minusMonths(1);
        }else{
            return LocalDateTime.now().minusDays(7);
        }
    }
}
