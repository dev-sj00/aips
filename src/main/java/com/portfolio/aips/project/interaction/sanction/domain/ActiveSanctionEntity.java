package com.portfolio.aips.project.interaction.sanction.domain;

import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


//배치 하루마다 삭제, 로그인 시 suspension 확인
@Entity
@Table(name="active_sanction")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ActiveSanctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="active_sanction_pk")
    private Long pk;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "target_user_pk",
            insertable = false,
            updatable = false
    )
    private UsersEntity targetUser;

    @Column(name = "target_user_pk", nullable = false)
    private Long targetUserPk;



    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;


    public void updateEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }


}
