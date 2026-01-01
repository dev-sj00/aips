package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command;



import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.repo.ArchiveELTrendingSearchLogRedisRepository;

// param: createDateTime 새로운 키워드 날짜
public record RedisSaveCommand(String keyword, String docCount, String createDateTime, SearchDateRange range, int score)  {
}
