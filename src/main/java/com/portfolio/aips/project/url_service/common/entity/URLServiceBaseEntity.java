package com.portfolio.aips.project.url_service.common.entity;

import com.portfolio.aips.project.utils.enums.LLMType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class URLServiceBaseEntity {
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name="llm_type", nullable=false)
    private LLMType llmType;

    @Column(length=200)
    private String description;

    @Column(name = "site_slug", nullable = false, unique = true, length = 255)
    private String siteSlug;

    @CreatedDate
    @Column(name = "created_date_time", updatable = false)
    private LocalDateTime createdDateTime;



}
