package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums;

import lombok.Getter;

@Getter
public enum SearchDateRange {

    DAILY("now-1d/d", "now/d",          // current: 최근 1일
            "now-2d/d", "now-1d/d"),     // prev: 그 전 1일

    THREE_DAYS("now-3d/d", "now/d",     // current: 최근 3일
            "now-6d/d", "now-3d/d"), // prev: 그 전 3일

    WEEK("now-7d/d", "now/d",           // current: 최근 7일
            "now-14d/d", "now-7d/d");      // prev: 그 전 7일


    private final String currentGte;
    private final String currentLte;

    private final String prevGte;
    private final String prevLte;


    SearchDateRange(String currentGte, String currentLte, String prevGte, String prevLte) {
        this.currentGte = currentGte;
        this.currentLte = currentLte;
        this.prevGte = prevGte;
        this.prevLte = prevLte;
    }
}
