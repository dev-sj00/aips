package com.portfolio.aips.project.elastic_search.archive.service;

import com.portfolio.aips.project.elastic_search.archive.document.ArchiveDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el.ArchiveELService;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_auto_complete.ArchiveELAutoCompleteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ArchiveELAutoCompleteServiceTest {
        private final ArchiveELAutoCompleteService archiveELAutoCompleteService;
        private final ArchiveELService archiveELService;

        @Autowired
    ArchiveELAutoCompleteServiceTest(ArchiveELAutoCompleteService archiveELAutoCompleteService, ArchiveELService archiveELService) {
        this.archiveELAutoCompleteService = archiveELAutoCompleteService;
            this.archiveELService = archiveELService;
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
        archiveDocument.setDescription("fgvwefewfw");
        archiveDocument.setPopularityScore(1L);
        archiveDocument.setTags(List.of("책상", "abc abc"));

        archiveELService.save(archiveDocument);


        archiveDocument.setTitle("qw32");
        archiveDocument.setDescription("fgvwefewfw");
        archiveDocument.setPopularityScore(1L);
        archiveDocument.setTags(List.of("책상 서랍", "32"));
        archiveELService.save(archiveDocument);

        

        List<String> result = archiveELAutoCompleteService.autocomplete("책상");



        for(String s : result)
        {
            System.out.println("result: " + s);
        }
        
    }
}