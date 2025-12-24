package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums;

import lombok.Getter;

@Getter
public enum SearchDateRange {
    WEEK("now-7d/d", "now/d"),
    MONTH("now-1M/M", "now/M"),
    YEAR("now-1y/y", "now/y");


    private String gte;
    private String lte;


    SearchDateRange(String gte, String lte) {
        this.gte = gte;
        this.lte = lte;
    }
}
