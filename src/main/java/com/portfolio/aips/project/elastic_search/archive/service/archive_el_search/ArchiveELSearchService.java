package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search;

import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveDocument;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.command.SearchByConditionCommand;
import com.portfolio.aips.project.elastic_search.archive.service.archive_el_search.command.SearchByConditionCreateAtCommand;

import java.io.IOException;
import java.util.List;

public interface ArchiveELSearchService {
    List<ArchiveDocument> searchAll(String keyword) throws IOException;
    List<ArchiveDocument> searchByCondition(SearchByConditionCommand command) throws IOException;
}
