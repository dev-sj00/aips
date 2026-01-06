package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.repo;

import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.GetTrendingKeywordsCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator.command.CalculateScoreCommand;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public interface ArchiveELTrendingSearchLogElasticRepository {
    HashMap<String, CalculateScoreCommand> findBySearchDateRangeAndCommand(SearchDateRange rang, GetTrendingKeywordsCommand command) throws IOException, URISyntaxException;
}
