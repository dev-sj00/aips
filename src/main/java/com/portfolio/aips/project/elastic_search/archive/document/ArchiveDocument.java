package com.portfolio.aips.project.elastic_search.archive.document;

import com.portfolio.aips.project.url_service.common.enums.URLStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArchiveDocument {
    private String pk;
    private String title;
    private URLStatus urlStatus;
    private String description;
    private String siteSlug;
    private LocalDateTime createdDateTime;
    private Long userPk;
    private Long popularityScore;
    private List<String> tags;
}
