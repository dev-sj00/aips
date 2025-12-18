package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search;

import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el.ArchiveELService;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.command.SearchByConditionCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.enums.SearchSortType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;


@SpringBootTest
class ArchiveELSearchServiceTest {


    private final ArchiveELSearchService archiveELSearchService;
    private final ArchiveELService archiveELService;

    @Autowired
    ArchiveELSearchServiceTest(ArchiveELSearchService archiveELSearchService, ArchiveELService archiveELService) {
        this.archiveELSearchService = archiveELSearchService;
        this.archiveELService = archiveELService;
    }


    @Test
    void searchByCondition_shouldBuildFilterCorrectly() throws IOException {

        OffsetDateTime baseTime = OffsetDateTime.now(ZoneOffset.UTC);

        List<ArchiveDocument> testDocuments = List.of(
                new ArchiveDocument() {{
                    setTitle("spring msa config server");
                    setDescription("ABC");
                    setPopularityScore(1L);
                    setTags(List.of("kafka", "graphql"));
                    setLlmType("chatgpt");
                    setCreatedDateTime(baseTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    setTopic("IT");
                }},
                new ArchiveDocument() {{
                    setTitle("spring cloud gateway example");
                    setDescription("Gateway tutorial for microservices");
                    setPopularityScore(2L);
                    setTags(List.of("spring", "gateway"));
                    setLlmType("gpt-4");
                    setCreatedDateTime(baseTime.minusMinutes(10).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    setTopic("IT");
                }},
                new ArchiveDocument() {{
                    setTitle("react frontend integration");
                    setDescription("Frontend integration with Spring Boot API");
                    setPopularityScore(3L);
                    setTags(List.of("react", "frontend"));
                    setLlmType("chatgpt");
                    setCreatedDateTime(baseTime.plusMinutes(2).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    setTopic("Web");
                }},
                new ArchiveDocument() {{
                    setTitle("kafka message streaming");
                    setDescription("Streaming messages with Kafka topics");
                    setPopularityScore(4L);
                    setTags(List.of("kafka", "streaming"));
                    setLlmType("gpt-4");
                    setCreatedDateTime(baseTime.plusMinutes(3).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    setTopic("IT");
                }},
                new ArchiveDocument() {{
                    setTitle("graphql api design");
                    setDescription("Designing GraphQL APIs for microservices");
                    setPopularityScore(5L);
                    setTags(List.of("graphql", "api"));
                    setLlmType("chatgpt");
                    setCreatedDateTime(baseTime.plusMinutes(4).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    setTopic("IT");
                }}
        );

        // 저장
        testDocuments.forEach(archiveELService::save);



        SearchByConditionCommand command = new SearchByConditionCommand(
                "spring",          // keyword
                "IT",              // topic
                SearchSortType.Latest, // sort type
                0,                 // pageFrom
                15,                // pageSize
                null//SearchDateRange.SIX_MONTHS // dateRange (enum)
        );

        List<ArchiveDocument> result = archiveELSearchService.searchByCondition(command);


        for(ArchiveDocument doc :  result){
            System.out.println(doc.getTitle());
        }

    }
}