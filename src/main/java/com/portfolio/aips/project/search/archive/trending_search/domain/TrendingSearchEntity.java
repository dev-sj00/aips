package com.portfolio.aips.project.search.archive.trending_search.domain;

import jakarta.persistence.*;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "trending_search")
@Setter
@EntityListeners(AuditingEntityListener.class)
public class TrendingSearchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trending_search_pk")
    private Long pk;

    @Column(name = "keyword", nullable = false, length = 100)
    private String keyword;

    @Column(name = "doc_count", nullable = false)
    private long docCount;

    @CreatedDate
    @Column(name = "create_date_time", nullable = false, updatable = false)
    private LocalDateTime createDateTime;


    @Column(name = "score", nullable = false)
    private double score;

}
