package com.portfolio.aips.project.elastic_search.archive.dto;

import com.portfolio.aips.project.url_service.common.enums.URLStatus;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class ArchiveDocument {
    private String pk;
    private String title;
    private URLStatus urlStatus;
    private String description;
    private String siteSlug;
    private String createdDateTime;
    private Long userPk;
    private Long popularityScore;
    private List<String> tags;
    private String llmType;
    private String topic;
    private Long viewCount;
}
