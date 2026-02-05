package com.portfolio.aips.project.statistics.domain.entity;

import com.portfolio.aips.project.interaction.common.enums.BoardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "board_statistics")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class BoardStatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

    @Enumerated(EnumType.STRING)
    @Column(name="board_type", nullable=false)
    private BoardType boardType;


    @Column(name = "submit_count", nullable = false)
    private Long submitCount;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDate createdDate;


}
