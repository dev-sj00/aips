package com.portfolio.aips.project.archived.entity;


import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "archived")
@Entity
public class ArchivedEntity {

    @Id
    @Column(name = "user_pk")
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


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk")
    private UsersEntity users;




}
