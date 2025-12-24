package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search_log.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.analyze.AnalyzeToken;
import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveSearchLogDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el.ArchiveELService;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.GetTrendingKeywordsCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.ArchiveELTrendingSearchLogService;
import com.portfolio.aips.project.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ArchiveELSearchLogServiceTest {

    @Autowired
    private ElasticsearchClient client;

    @Autowired
    private ArchiveELSearchLogService archiveELSearchLogService;

    @Autowired
    private ArchiveELTrendingSearchLogService archiveELTrendingSearchLogService;

    @Autowired
    private ArchiveELService archiveELService;

    @Test
    public void analyzeTextTest() throws IOException {

        //given
        AnalyzeRequest request = AnalyzeRequest.of(a -> a
                .index("archive_search_log") // index analyzer 사용
                .analyzer("stat_analyzer")  // analyzer 이름
                .text("전국 치킨집 리뷰합니다.")
        );

        AnalyzeResponse response = client.indices().analyze(request);


        //when
        // 토큰 리스트 추출
        List<String> result = response.tokens().stream()
                .map(AnalyzeToken::token)
                .toList();

        //then
        assertNotNull(result);
        for (String token : result) {

            System.out.println(token);
        }

    }

    @Test
    public void save_test() throws IOException {
        archiveELSearchLogService.save("키보드 마우스 모니터 추천", 1L);
    }


    @Test
    public void get_trending_keyword_test() throws URISyntaxException, IOException {


        List<String> keywords = Arrays.asList("치킨집", "치킨집", "치킨집", "라면집", "라면집", "라면집");

        for (String keyword : keywords) {
            ArchiveSearchLogDocument saveDoc = ArchiveSearchLogDocument
                    .builder()
                    .hasFiltered(false)                        // Filter applied or not
                    .queryRaw(keyword)                         // Original search query
                    .queryStat(keyword)                        // Used for aggregation
                    .userNickName("testUser")                  // Example user nickname
                    .createdDateTime(ZonedDateTime.now()
                            .minusDays(1) // 1일 빼기
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) // Current timestamp
                    .tokens(Collections.singletonList(keyword))            // Tokenized version
                    .build();

            archiveELService.save(saveDoc);                // Save to Elasticsearch
        }

        archiveELTrendingSearchLogService.getTrendingKeywords(
                new GetTrendingKeywordsCommand(SearchDateRange.WEEK, 10)
        );
    }
}