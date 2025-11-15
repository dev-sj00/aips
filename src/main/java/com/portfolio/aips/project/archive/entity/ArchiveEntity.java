package com.portfolio.aips.project.archive.entity;


import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    @Column(name = "archive_link", nullable = false, length = 255)
    private String archiveLink; // 아카이브 링크

    @Column(name = "site_alive", nullable = false)
    private boolean siteAlive = true; // 현재 페이지가 살아있는지

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
