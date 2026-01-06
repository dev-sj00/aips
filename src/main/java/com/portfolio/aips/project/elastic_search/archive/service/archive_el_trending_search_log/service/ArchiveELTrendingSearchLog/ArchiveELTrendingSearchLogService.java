package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.ArchiveELTrendingSearchLog;


import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.GetTrendingKeywordsCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.result.GetTrendingKeywordsResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

//향후 변경될 가능성 있음
public interface ArchiveELTrendingSearchLogService {
    List<GetTrendingKeywordsResult> getDailyTrending(GetTrendingKeywordsCommand command) throws URISyntaxException, IOException;
    List<GetTrendingKeywordsResult> get3DayTrending(GetTrendingKeywordsCommand command) throws URISyntaxException, IOException;
    List<GetTrendingKeywordsResult> get7DayTrending(GetTrendingKeywordsCommand command) throws URISyntaxException, IOException;
}
