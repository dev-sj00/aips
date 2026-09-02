package com.portfolio.aips.project.elastic_search.archive.service;

import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el.ArchiveELService;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_auto_complete.ArchiveELAutoCompleteService;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.ArchiveELSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@SpringBootTest
class ArchiveELAutoCompleteServiceTest {
        private final ArchiveELAutoCompleteService archiveELAutoCompleteService;
        private final ArchiveELService archiveELService;
        private final ArchiveELSearchService archiveELSearchService;

        @Autowired
    ArchiveELAutoCompleteServiceTest(ArchiveELAutoCompleteService archiveELAutoCompleteService, ArchiveELService archiveELService, ArchiveELSearchService archiveELSearchService) {
        this.archiveELAutoCompleteService = archiveELAutoCompleteService;
            this.archiveELService = archiveELService;
            this.archiveELSearchService = archiveELSearchService;
        }

    @Test
    void getAutoComplete() throws URISyntaxException, IOException {
        archiveELService.save(
                ArchiveDocument.builder()
                        .title("abc")
                        .description("abc")
                        .popularityScore(1.0)
                        .tags(List.of("abc review", "abc good"))
                        .build()
        );

        archiveELService.save(
                ArchiveDocument.builder()
                        .title("abc2")
                        .description("fgvwefewfw2")
                        .popularityScore(1.0)
                        .tags(List.of("책상", "abc abc"))
                        .build()
        );

        archiveELService.save(
                ArchiveDocument.builder()
                        .title("qw32")
                        .description("fgvwefewfw")
                        .popularityScore(1.0)
                        .tags(List.of("책상 서랍", "32"))
                        .build()
        );

        

        List<String> result = archiveELAutoCompleteService.autocomplete("책상");



        for(String s : result)
        {
            System.out.println("자동 완성 result: " + s);
        }

        archiveELSearchService.searchAll("fgvwefewfw");

        
    }
}