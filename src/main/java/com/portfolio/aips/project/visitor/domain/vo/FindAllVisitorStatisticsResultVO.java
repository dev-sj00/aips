package com.portfolio.aips.project.visitor.domain.vo;


import java.time.LocalDate;

public record FindAllVisitorStatisticsResultVO(LocalDate startDate, LocalDate endDate, Long totalVisitCount) {


    public FindAllVisitorStatisticsResultVO(LocalDate startDate, Long totalVisitCount) {
        // 여기서 "this(...)"를 통해 메인 생성자를 호출하면서 endDate를 계산해 넣음
        this(startDate, startDate.plusDays(6), totalVisitCount);
    }

}
