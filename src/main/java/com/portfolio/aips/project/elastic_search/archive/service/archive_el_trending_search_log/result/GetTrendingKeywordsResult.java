package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.result;

public record GetTrendingKeywordsResult(String rank, String keyword, String searchCount, boolean isNew) {
}
