package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command;

import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
import lombok.Setter;


public record GetTrendingKeywordsCommand(int reqSize) {
}
