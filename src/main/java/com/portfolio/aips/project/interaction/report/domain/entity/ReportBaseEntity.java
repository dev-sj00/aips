package com.portfolio.aips.project.interaction.report.domain.entity;

import com.portfolio.aips.project.interaction.report.domain.model.ReportType;
import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class ReportBaseEntity {


    @Column(name="report_url")
    String reportUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "target_user_pk",
            insertable = false,
            updatable = false
    )
    private UsersEntity targetUser;

    @Column(name = "target_user_pk")
    private Long targetUserPk;


    @Column(name = "reporter_user_pk")
    private Long reporterUserPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reporter_user_pk",
            insertable = false,
            updatable = false
    )
    private UsersEntity reporterUser;

    @Column(name = "report_content", length = 300, nullable = false)
    @Size(max = 300)
    @NotBlank
    private String reportContent;


    @CreatedDate
    @Column(name = "created_date_time", nullable = false, updatable = false)
    private LocalDateTime createdDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, updatable = false)
    private ReportType reportType;

}
