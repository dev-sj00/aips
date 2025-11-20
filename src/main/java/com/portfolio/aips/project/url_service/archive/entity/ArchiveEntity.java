package com.portfolio.aips.project.url_service.archive.entity;


import com.portfolio.aips.project.tags.entity.BoardTagsEntity;
import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "archive")
@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ArchiveEntity {

    @Id
    @Column(name = "archive_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(name = "title", nullable = false, length = 100)
    private String title;


    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL,  orphanRemoval = true)
    private URLStatusEntity urlStatusEntity;

    @Column(length=200)
    private String description;

    @Column(name = "site_slug", nullable = false, unique = true, length = 255)
    private String siteSlug;

    @CreatedDate
    @Column(name = "created_date_time", updatable = false)
    private LocalDateTime createdDateTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_pk")
    private UsersEntity users;


    @Column(name = "user_pk", insertable=false, updatable=false)
    private Long userPk;





}
