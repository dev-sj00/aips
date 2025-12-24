package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command;

import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;

public record GetTrendingKeywordsCommand(SearchDateRange searchDateRange, int size) {
}
