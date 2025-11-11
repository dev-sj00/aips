package com.portfolio.aips.project.archive.entity;


import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Table(name = "archive")
@Entity
@Setter
@Getter
public class ArchiveEntity {

    @Id
    @Column(name = "archive_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;


    @Column(nullable = false, length = 100)
    private String title;


    @Column(nullable = false, length = 255)
    private String archiveLink; //아카이브 링크


    @Column(nullable = false)
    private boolean siteAlive = true; //현재 페이지가 살아있는지

    @Column(nullable = false, unique = true, length = 255)
    private String siteSlug;


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDateTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_pk")
    private UsersEntity users;






}
