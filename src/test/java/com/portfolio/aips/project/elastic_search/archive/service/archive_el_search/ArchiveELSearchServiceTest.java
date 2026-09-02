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
                ArchiveDocument.builder()
                        .title("spring msa config server")
                        .description("ABC")
                        .popularityScore(1.0)
                        .tags(List.of("kafka", "graphql"))
                        .llmType("chatgpt")
                        .createdDateTime(baseTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .topic("IT")
                        .build(),

                ArchiveDocument.builder()
                        .title("spring cloud gateway example")
                        .description("Gateway tutorial for microservices")
                        .popularityScore(2.0)
                        .tags(List.of("spring", "gateway"))
                        .llmType("gpt-4")
                        .createdDateTime(baseTime.minusMinutes(10).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .topic("IT")
                        .build(),

                ArchiveDocument.builder()
                        .title("react frontend integration")
                        .description("Frontend integration with Spring Boot API")
                        .popularityScore(3.0)
                        .tags(List.of("react", "frontend"))
                        .llmType("chatgpt")
                        .createdDateTime(baseTime.plusMinutes(2).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .topic("Web")
                        .build(),

                ArchiveDocument.builder()
                        .title("kafka message streaming")
                        .description("Streaming messages with Kafka topics")
                        .popularityScore(4.0)
                        .tags(List.of("kafka", "streaming"))
                        .llmType("gpt-4")
                        .createdDateTime(baseTime.plusMinutes(3).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .topic("IT")
                        .build(),

                ArchiveDocument.builder()
                        .title("graphql api design")
                        .description("Designing GraphQL APIs for microservices")
                        .popularityScore(5.0)
                        .tags(List.of("graphql", "api"))
                        .llmType("chatgpt")
                        .createdDateTime(baseTime.plusMinutes(4).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .topic("IT")
                        .build()
        );

        // 저장
        testDocuments.forEach(archiveELService::save);



        SearchByConditionCommand command = new SearchByConditionCommand(
                "spring",          // keyword
                "IT",              // topic
                SearchSortType.Latest, // sort type
                0,                 // pageFrom
                15,                // pageSize
                null,//SearchDateRange.SIX_MONTHS // dateRange (enum)
                "chatgpt"
        );

        List<ArchiveDocument> result = archiveELSearchService.searchByCondition(command);


        for(ArchiveDocument doc :  result){
            System.out.println(doc.getTitle());
        }

    }
}