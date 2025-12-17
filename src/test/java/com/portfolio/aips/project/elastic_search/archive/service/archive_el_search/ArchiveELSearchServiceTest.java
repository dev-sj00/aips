package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search;

import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.command.SearchByConditionCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.enums.SearchDateRange;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.enums.SearchSortType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ArchiveELSearchServiceTest {


    private final ArchiveELSearchService archiveELSearchService;

    @Autowired
    ArchiveELSearchServiceTest(ArchiveELSearchService archiveELSearchService) {
        this.archiveELSearchService = archiveELSearchService;
    }


    @Test
    void searchByCondition_shouldBuildFilterCorrectly() throws IOException {
        SearchByConditionCommand command = new SearchByConditionCommand(
                "spring",          // keyword
                "IT",              // topic
                SearchSortType.Latest, // sort type
                0,                 // pageFrom
                15,                // pageSize
                null//SearchDateRange.SIX_MONTHS // dateRange (enum)
        );

        archiveELSearchService.searchByCondition(command);

    }
}