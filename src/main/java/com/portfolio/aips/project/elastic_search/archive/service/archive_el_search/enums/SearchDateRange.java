package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.enums;

import lombok.Getter;

@Getter
public enum SearchDateRange {
    HOUR("now-1h/h", "now/h"),
    DAY("now-1d/d", "now/d"),
    WEEK("now-7d/d", "now/d"),
    MONTH("now-1M/M", "now/M"),
    THREE_MONTHS("now-3M/M", "now/M"),
    SIX_MONTHS("now-6M/M", "now/M"),
    YEAR("now-1y/y", "now/y");

    private String gte;
    private String lte;

    SearchDateRange(String gte, String lte) {
        this.gte = gte;
        this.lte = lte;
    }

    public void setRange(String gte, String lte) {
        this.gte = gte;
        this.lte = lte;
    }
}
