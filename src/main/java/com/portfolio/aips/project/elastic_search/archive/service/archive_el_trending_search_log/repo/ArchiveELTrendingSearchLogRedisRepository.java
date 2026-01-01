package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.repo;

import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.RedisSaveCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.result.GetTrendingKeywordsResult;

import java.util.List;

public interface ArchiveELTrendingSearchLogRedisRepository {

    void save(RedisSaveCommand command); //새로운 키워드인지 아닌지 확인 용도
    List<GetTrendingKeywordsResult> findAll(SearchDateRange range);
}
