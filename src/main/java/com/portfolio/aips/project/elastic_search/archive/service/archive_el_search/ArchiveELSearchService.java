package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search;

import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.command.SearchByConditionCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.command.SearchByConditionCreateAtCommand;

import java.io.IOException;

public interface ArchiveELSearchService {
    void searchAll(String keyword) throws IOException;
    void searchByCondition(SearchByConditionCommand command) throws IOException;
}
