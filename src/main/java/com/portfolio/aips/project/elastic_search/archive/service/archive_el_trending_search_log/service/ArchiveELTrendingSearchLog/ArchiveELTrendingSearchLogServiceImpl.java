package com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.ArchiveELTrendingSearchLog;


import co.elastic.clients.elasticsearch.ElasticsearchClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.GetTrendingKeywordsCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.result.GetTrendingKeywordsResult;
import com.portfolio.aips.project.utils.ESTemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveELTrendingSearchLogServiceImpl implements ArchiveELTrendingSearchLogService {

    private final ElasticsearchClient client;

    private static final int MAX_TREND_KEYWORD_RESULT = 50;

    private List<GetTrendingKeywordsResult> getTrendingKeywordsProc(SearchDateRange searchDateRange, GetTrendingKeywordsCommand command) throws URISyntaxException, IOException
    {


        String jsonTemplate = ESTemplateUtils.loadJson("elastic/queries/archive_get_trending_keywords.json");
        String jsonQuery =  getJsonQueryWithFormat(jsonTemplate, searchDateRange, command);


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


        JsonNode buckets = root.path("aggregations")
                .path("trending_keywords")
                .path("buckets");


        //구현 중
        for (JsonNode bucket : buckets) {
            String key = bucket.path("key").asText();
            long docCount = bucket.path("doc_count").asLong();
            log.info("{}: {}", key, docCount);
        }

        //redis proc



        return List.of();
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
