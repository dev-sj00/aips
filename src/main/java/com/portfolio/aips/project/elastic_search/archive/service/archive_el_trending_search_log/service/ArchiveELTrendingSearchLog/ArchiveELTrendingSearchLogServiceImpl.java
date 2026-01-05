package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.ArchiveELTrendingSearchLog;


import co.elastic.clients.elasticsearch.ElasticsearchClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.GetTrendingKeywordsCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.RedisSaveCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
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

    private final ElasticsearchClient client;

    private final TrendingScoreCalculator trendingScoreCalculator;
    private final ArchiveELTrendingSearchLogRedisRepository archiveELTrendingSearchLogRedisRepository;
    private final String archiveGetTrendingKeywordsTemplate;

    private List<GetTrendingKeywordsResult> getTrendingKeywordsProc(SearchDateRange searchDateRange, GetTrendingKeywordsCommand command) throws URISyntaxException, IOException
    {



        String jsonQuery =  getJsonQueryWithFormat(archiveGetTrendingKeywordsTemplate, searchDateRange, command);


        log.info("jsonQuery: {}", jsonQuery);
        Response response = ESTemplateUtils.responseBuilder(client)
                .body(jsonQuery)
                .url("/archive_search_log/_search")
                .method("POST")
                .execute();

        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        log.info("aggregations {}", responseBody);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);


        HashMap<String, CalculateScoreCommand> calculateScoreCommandMap = getCalculateCommandMap(root);


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

    private HashMap<String, CalculateScoreCommand> getCalculateCommandMap(JsonNode root) {
        JsonNode currentBuckets = root.path("aggregations")
                .path("periods")
                .path("buckets")
                .path("current")
                .path("trending_keywords")
                .path("buckets");

        JsonNode prevBuckets = root.path("aggregations")
                .path("periods")
                .path("buckets")
                .path("prev")
                .path("trending_keywords")
                .path("buckets");


        //구현 중


        HashMap<String, CalculateScoreCommand> resultMap = new HashMap<>();


        currentBuckets.forEach(b ->
                resultMap.put(b.path("key").asText(),
                        new CalculateScoreCommand(0, b.path("doc_count").asLong()))
        );


        prevBuckets.forEach(b-> {
            String key = b.path("key").asText();
            long docCount = b.path("doc_count").asLong();

            resultMap.computeIfPresent(key,
                    (k, v) -> new CalculateScoreCommand(docCount, v.currentDocCount()));
        });

        return resultMap;
    }

    private String getJsonQueryWithFormat(String jsonTemplate, SearchDateRange range, GetTrendingKeywordsCommand command) throws URISyntaxException, IOException {
        String currentGte = range.getCurrentGte();
        String currentLte = range.getCurrentLte();
        String prevGte = range.getPrevGte();
        String prevLte = range.getPrevLte();
        int reqSize = command.reqSize();


        return String.format(
                jsonTemplate,
                currentGte,
                currentLte,
                prevGte,
                prevLte,
                reqSize
        );
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
