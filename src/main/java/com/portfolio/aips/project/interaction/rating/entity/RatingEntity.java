package com.portfolio.aips.project.interaction.rating.entity;

import com.portfolio.aips.project.interaction.enums.BoardType;
import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "rating",
uniqueConstraints = {
@UniqueConstraint(
        name = "uk_rating_once",
        columnNames = {"board_id", "board_type", "rater_user_pk"}
)
    }
)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Setter
public class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_pk")
    private Long pk;

    /** 평가 대상 게시글 ID */
    @Column(name = "board_pk", nullable = false)
    private Long boardPk;

    /** 게시글 타입 */
    @Enumerated(EnumType.STRING)
    @Column(name = "board_type", nullable = false, length = 30)
    private BoardType boardType;

    /** 유용성 점수 */
    @Column(name = "usefulness_score", nullable = false)
    private int usefulnessScore;

    /** 신뢰도 점수 */
    @Column(name = "reliability_score", nullable = false)
    private int reliabilityScore;

    /** 재미 / 흥미 점수 */
    @Column(name = "fun_score", nullable = false)
    private int funScore;

    /** 평가한 유저 사용자 PK */
    @Column(name = "rater_user_pk", nullable = false, updatable = false)
    private Long raterUserPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "rater_user_pk",
            insertable = false,
            updatable = false
    )
    private UsersEntity raterUser;


    @CreatedDate
    @Column(name = "create_date_time", nullable = false, updatable = false)
    private LocalDateTime createDateTime;


    public void updateScores(
            int usefulnessScore,
            int reliabilityScore,
            int funScore
    ) {
        this.usefulnessScore = usefulnessScore;
        this.reliabilityScore = reliabilityScore;
        this.funScore = funScore;
    }

}
