package com.portfolio.aips.project.elastic_search.archive.document;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "archive")
public class ArchiveDocument {
    @Id
    private Long pk;

    @Field(type = FieldType.Text)
    private String title;

    // URLStatusEntity를 nested 타입으로 저장
    @Field(type = FieldType.Nested, includeInParent = true)
    private URLStatusDocument urlStatus;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String siteSlug;

    @Field(type = FieldType.Date)
    private LocalDateTime createdDateTime;

    @Field(type = FieldType.Keyword)
    private Long userPk;


}
