package com.portfolio.aips.project.statistics.app.router.statistics_usecase.result;

import java.time.LocalDate;

public record FindAllBoardStatisticsResult(LocalDate startDate, LocalDate endDate, Long submitCount) {

    public FindAllBoardStatisticsResult(LocalDate startDate, Long submitCount) {

        this(startDate, startDate.plusDays(6), submitCount);
    }
}
