package com.portfolio.aips.project.visitor.domain.vo;

import com.portfolio.aips.project.visitor.domain.enums.VisitorSortType;

import java.time.LocalDate;

public record FindAllVisitorStatisticsVO(int page, int size, VisitorSortType sortType) {


}
