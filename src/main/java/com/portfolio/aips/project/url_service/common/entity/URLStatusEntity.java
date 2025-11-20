package com.portfolio.aips.project.url_service.common.entity;

import com.portfolio.aips.project.url_service.common.enums.URLStatus;
import com.portfolio.aips.project.url_service.common.service.url_generator.enums.URLGeneratorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "url_status")
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
@Setter
public class URLStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "url_status_pk")
    private Long pk;


    @Enumerated(EnumType.STRING)
    @Column(name = "url_type", nullable = false)
    private URLGeneratorType urlType;


    @Enumerated(EnumType.STRING)
    @Column(name = "url_status", nullable = false)
    @Builder.Default
    private URLStatus urlStatus = URLStatus.ACTIVE; // 기본값 INVALID

    @Column(name = "url_link", nullable = false)
    private String urlLink;


    @Builder.Default
    @Column(name = "needs_verification", nullable = false)
    private boolean needsVerification = false; // 기본값 true

    @Builder.Default
    @Column(name = "is_created", nullable = false)
    private boolean isCreated = false; // 기본값 false

    @CreatedDate
    @Column(name = "valid_date_time", nullable = false)
    private LocalDateTime validDateTime;


    @CreatedDate
    @Column(name = "create_date_time", nullable = false, updatable = false)
    private LocalDateTime createDateTime;


    // 기본 생성자
    public URLStatusEntity() {
    }

}
