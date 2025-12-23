package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search_log.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.analyze.AnalyzeToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ArchiveELSearchLogServiceTest {

    @Autowired
    private ElasticsearchClient client;

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
}