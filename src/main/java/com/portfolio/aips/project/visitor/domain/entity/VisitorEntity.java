package com.portfolio.aips.project.visitor.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;


@Entity
@Table(name = "visitor")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VisitorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="visitor_pk")
    private Long pk;

    @Column(name = "visit_count", nullable = false)
    private Long visitCount;

    @Column(name = "created_date_time", nullable = false, updatable = false)
    @CreatedDate
    private LocalDate createdDate;

}
