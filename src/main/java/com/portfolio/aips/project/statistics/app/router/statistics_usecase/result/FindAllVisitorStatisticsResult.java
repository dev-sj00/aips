package com.portfolio.aips.project.statistics.app.router.statistics_usecase.result;


import java.time.LocalDate;

public record FindAllVisitorStatisticsResult(LocalDate startDate, LocalDate endDate, Long totalVisitCount) {


    public FindAllVisitorStatisticsResult(LocalDate startDate, Long totalVisitCount) {
        // 여기서 "this(...)"를 통해 메인 생성자를 호출하면서 endDate를 계산해 넣음
        this(startDate, startDate.plusDays(6), totalVisitCount);
    }

}
