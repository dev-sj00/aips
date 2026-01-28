package com.portfolio.aips.project.url_service.archive.entity;


import com.portfolio.aips.project.tags.entity.BoardTagsEntity;
import com.portfolio.aips.project.url_service.common.entity.URLServiceBaseEntity;
import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.users.entity.UsersEntity;
import com.portfolio.aips.project.utils.enums.LLMType;
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
public class ArchiveEntity extends URLServiceBaseEntity {

    @Id
    @Column(name = "archive_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL,  orphanRemoval = true)
    @JoinColumn(name = "url_status_pk", referencedColumnName = "url_status_pk")
    private URLStatusEntity urlStatusEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_pk")
    private UsersEntity users;


    @Column(name = "user_pk", insertable=false, updatable=false)
    private Long userPk;





}
