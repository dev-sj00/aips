package com.portfolio.aips.project.interaction.view.entity;

import com.portfolio.aips.project.interaction.enums.BoardType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "view")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ViewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_pk")
    private Long pk;

    @Column(name = "board_pk", nullable = false)
    private Long boardPk;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_type", nullable = false, length = 30)
    private BoardType boardType;

    @Column(name = "view_count", nullable = false)
    private long viewCount;
}
