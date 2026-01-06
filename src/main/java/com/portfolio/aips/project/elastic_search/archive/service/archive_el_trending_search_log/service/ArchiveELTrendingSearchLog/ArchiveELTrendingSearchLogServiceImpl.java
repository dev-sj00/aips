package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.ArchiveELTrendingSearchLog;


import co.elastic.clients.elasticsearch.ElasticsearchClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.GetTrendingKeywordsCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.RedisSaveCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.repo.ArchiveELTrendingSearchLogElasticRepository;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.repo.ArchiveELTrendingSearchLogRedisRepository;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.result.GetTrendingKeywordsResult;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator.TrendingScoreCalculator;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.TrendingScoreCalculator.command.CalculateScoreCommand;
import com.portfolio.aips.project.utils.DateUtils;
import com.portfolio.aips.project.utils.ESTemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveELTrendingSearchLogServiceImpl implements ArchiveELTrendingSearchLogService {


    private final TrendingScoreCalculator trendingScoreCalculator;
    private final ArchiveELTrendingSearchLogRedisRepository archiveELTrendingSearchLogRedisRepository;
    private final ArchiveELTrendingSearchLogElasticRepository archiveELTrendingSearchLogElasticRepository;

    private List<GetTrendingKeywordsResult> getTrendingKeywordsProc(SearchDateRange searchDateRange, GetTrendingKeywordsCommand command) throws URISyntaxException, IOException
    {




        HashMap<String, CalculateScoreCommand> calculateScoreCommandMap = archiveELTrendingSearchLogElasticRepository.findBySearchDateRangeAndCommand(searchDateRange, command);

        List<RedisSaveCommand> results= new ArrayList<>();
        calculateScoreCommandMap.forEach((key, value) -> {
            double score = trendingScoreCalculator.calculateScore(value);

            results.add(new RedisSaveCommand(key, String.valueOf(value.currentDocCount()), DateUtils.getDateTimeNow(), searchDateRange, score));

        });


        //redis proc
        for(RedisSaveCommand redisSaveCommand : results) {
            archiveELTrendingSearchLogRedisRepository.save(redisSaveCommand);
        }

        return archiveELTrendingSearchLogRedisRepository.findAll(searchDateRange);


    }






    @Override
    public List<GetTrendingKeywordsResult> getDailyTrending(GetTrendingKeywordsCommand command) throws URISyntaxException, IOException {

        return getTrendingKeywordsProc(SearchDateRange.DAILY, command);
    }

    @Override

    public List<GetTrendingKeywordsResult> get3DayTrending(GetTrendingKeywordsCommand command) throws URISyntaxException, IOException {
        return getTrendingKeywordsProc(SearchDateRange.THREE_DAYS, command);
    }

    @Override
    public List<GetTrendingKeywordsResult> get7DayTrending(GetTrendingKeywordsCommand command) throws URISyntaxException, IOException {
        return getTrendingKeywordsProc(SearchDateRange.WEEK, command);
    }
}
