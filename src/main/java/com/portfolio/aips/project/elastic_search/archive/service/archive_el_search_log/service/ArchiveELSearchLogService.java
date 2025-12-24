package com.portfolio.aips.project.elastic_search.archive.service.archive_el_search_log.service;

import com.portfolio.aips.project.elastic_search.archive.dto.ArchiveSearchLogDocument;

import java.io.IOException;
import java.util.List;

public interface ArchiveELSearchLogService {
        void save(String queryRaw, Long userPk) throws IOException;
        void getLogs(List<ArchiveSearchLogDocument> docs, int pageNo, int pageSize);

}
