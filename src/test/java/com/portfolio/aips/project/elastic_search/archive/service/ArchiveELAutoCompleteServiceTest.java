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
        ArchiveDocument archiveDocument = new ArchiveDocument();

        archiveDocument.setTitle("abc");
        archiveDocument.setDescription("abc");
        archiveDocument.setPopularityScore(1L);
        archiveDocument.setTags(List.of("abc review", "abc good"));
        archiveELService.save(archiveDocument);

        archiveDocument.setTitle("abc2");
        archiveDocument.setDescription("fgvwefewfw2");
        archiveDocument.setPopularityScore(1L);
        archiveDocument.setTags(List.of("책상", "abc abc"));

        archiveELService.save(archiveDocument);


        archiveDocument.setTitle("qw32");
        archiveDocument.setDescription("fgvwefewfw");
        archiveDocument.setPopularityScore(1L);
        archiveDocument.setTags(List.of("책상 서랍", "32"));
        archiveELService.save(archiveDocument);

        

        List<String> result = archiveELAutoCompleteService.autocomplete("책상 서");



        for(String s : result)
        {
            System.out.println("자동 완성 result: " + s);
        }

        archiveELSearchService.searchAll("fgvwefewfw");

        
    }
}