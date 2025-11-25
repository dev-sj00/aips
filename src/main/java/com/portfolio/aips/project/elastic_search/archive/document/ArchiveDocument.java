package com.portfolio.aips.project.elastic_search.archive.document;

import com.portfolio.aips.project.url_service.common.enums.URLStatus;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "archive")
@Getter
@Setter
public class ArchiveDocument {

    @Id
    private String pk;

    @Field(type = FieldType.Text)
    private String title;

    // URLStatusEntity를 nested 타입으로 저장
    @Field(type = FieldType.Nested, includeInParent = true)
    private URLStatus urlStatus;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String siteSlug;

    @Field(type = FieldType.Date)
    private LocalDateTime createdDateTime;

    @Field(type = FieldType.Keyword)
    private Long userPk;


}
