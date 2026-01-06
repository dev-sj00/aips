package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search_log.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.analyze.AnalyzeToken;
import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveSearchLogDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el.ArchiveELService;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.command.GetTrendingKeywordsCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.result.GetTrendingKeywordsResult;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_trending_search_log.service.ArchiveELTrendingSearchLog.ArchiveELTrendingSearchLogService;
import com.portfolio.aips.project.search.archive.trending_search.service.TrendingSearchScheduleService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private TrendingSearchScheduleService trendingSearchScheduleService;

    @Autowired
    private EntityManager em;

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
    @Transactional
    public void trendingSearchLogTest() throws IOException, URISyntaxException {
        List<String> keywords = Arrays.asList("치킨집", "치킨집", "치킨집", "라면집", "라면집", "라면집");


        for (String keyword : keywords) { //current
            ArchiveSearchLogDocument saveDoc = ArchiveSearchLogDocument
                    .builder()
                    .hasFiltered(false)                        // Filter applied or not
                    .queryRaw(keyword)                         // Original search query
                    .queryStat(keyword)                        // Used for aggregation
                    .userNickName("testUser")                  // Example user nickname
                    .createdDateTime(ZonedDateTime.now(ZoneOffset.UTC)
                            .truncatedTo(ChronoUnit.DAYS)
                            .minusDays(1) // 1일 빼기
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) // Current timestamp
                    .tokens(Collections.singletonList(keyword))            // Tokenized version
                    .build();

            archiveELService.save(saveDoc);                // Save to Elasticsearch
        }

        for (String keyword : keywords) { //prev
            ArchiveSearchLogDocument saveDoc = ArchiveSearchLogDocument
                    .builder()
                    .hasFiltered(false)                        // Filter applied or not
                    .queryRaw(keyword)                         // Original search query
                    .queryStat(keyword)                        // Used for aggregation
                    .userNickName("testUser")                  // Example user nickname
                    .createdDateTime(ZonedDateTime.now(ZoneOffset.UTC)
                            .truncatedTo(ChronoUnit.DAYS)
                            .minusDays(9) // 1일 빼기
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) // Current timestamp
                    .tokens(Collections.singletonList(keyword))            // Tokenized version
                    .build();

            archiveELService.save(saveDoc);                // Save to Elasticsearch
        }


        trendingSearchScheduleService.save();

        em.flush();
    }


    @Test
    public void get_trending_keyword_test() throws URISyntaxException, IOException {

        List<String> keywords = Arrays.asList("치킨집", "치킨집", "치킨집", "라면집", "라면집", "라면집");


        for (String keyword : keywords) { //current
            ArchiveSearchLogDocument saveDoc = ArchiveSearchLogDocument
                    .builder()
                    .hasFiltered(false)                        // Filter applied or not
                    .queryRaw(keyword)                         // Original search query
                    .queryStat(keyword)                        // Used for aggregation
                    .userNickName("testUser")                  // Example user nickname
                    .createdDateTime(ZonedDateTime.now(ZoneOffset.UTC)
                            .truncatedTo(ChronoUnit.DAYS)
                            .minusDays(1) // 1일 빼기
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) // Current timestamp
                    .tokens(Collections.singletonList(keyword))            // Tokenized version
                    .build();

            archiveELService.save(saveDoc);                // Save to Elasticsearch
        }

        for (String keyword : keywords) { //prev
            ArchiveSearchLogDocument saveDoc = ArchiveSearchLogDocument
                    .builder()
                    .hasFiltered(false)                        // Filter applied or not
                    .queryRaw(keyword)                         // Original search query
                    .queryStat(keyword)                        // Used for aggregation
                    .userNickName("testUser")                  // Example user nickname
                    .createdDateTime(ZonedDateTime.now(ZoneOffset.UTC)
                            .truncatedTo(ChronoUnit.DAYS)
                            .minusDays(2) // 1일 빼기
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) // Current timestamp
                    .tokens(Collections.singletonList(keyword))            // Tokenized version
                    .build();

            archiveELService.save(saveDoc);                // Save to Elasticsearch
        }


        List<GetTrendingKeywordsResult> result = archiveELTrendingSearchLogService.getDailyTrending(
                new GetTrendingKeywordsCommand( 10)
        );

        for(GetTrendingKeywordsResult getTrendingKeywordsResult : result){
            System.out.println(getTrendingKeywordsResult);
        }
    }

    @Test
    //치킨집 → current 3 / prev 2 rank 1
    //라면집 → current 3 / prev 2 rank 0
    public void get_trending_keywords_test2() throws URISyntaxException, IOException {
        List<String> keywords = Arrays.asList("치킨집", "치킨집", "치킨집", "라면집", "라면집", "라면집");

// --- Current Data (어제, 동일)
        for (String keyword : keywords) {
            ArchiveSearchLogDocument saveDoc = ArchiveSearchLogDocument
                    .builder()
                    .hasFiltered(false)
                    .queryRaw(keyword)
                    .queryStat(keyword)
                    .userNickName("testUser")
                    .createdDateTime(ZonedDateTime.now(ZoneOffset.UTC)
                            .truncatedTo(ChronoUnit.DAYS)
                            .minusDays(1) // current = 어제
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .tokens(Collections.singletonList(keyword))
                    .build();

            archiveELService.save(saveDoc);
        }

// --- Previous Data (그제, current 대비 다르게)
        Map<String, Integer> prevCounts = Map.of(
                "치킨집", 2,  // current 3번 대비 prev 1번
                "라면집", 1   // current 3번 대비 prev 2번
        );

        for (Map.Entry<String, Integer> entry : prevCounts.entrySet()) {
            String keyword = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                ArchiveSearchLogDocument saveDoc = ArchiveSearchLogDocument
                        .builder()
                        .hasFiltered(false)
                        .queryRaw(keyword)
                        .queryStat(keyword)
                        .userNickName("testUser")
                        .createdDateTime(ZonedDateTime.now(ZoneOffset.UTC)
                                .truncatedTo(ChronoUnit.DAYS)
                                .minusDays(2) // prev = 그제
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .tokens(Collections.singletonList(keyword))
                        .build();

                archiveELService.save(saveDoc);
            }
        }

        List<GetTrendingKeywordsResult> result = archiveELTrendingSearchLogService.getDailyTrending(
                new GetTrendingKeywordsCommand( 10)
        );

        for(GetTrendingKeywordsResult getTrendingKeywordsResult : result){
            System.out.println(getTrendingKeywordsResult);
        }


    }
}