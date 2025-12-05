package com.portfolio.aips.project.invite.entity;

import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity(name = "invited_user_list")
@Getter
@Setter
public class InviteUserListEntity {


    @Id
    @Column(name = "invited_user_list_pk")
/*    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invited_user_list_seq")
    @SequenceGenerator(
            name = "invited_user_list_seq",
            sequenceName = "invited_user_list_seq",
            allocationSize = 20   // Hibernate batch size와 맞춰주는 게 좋음
    ) 불필요*/
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", insertable = false, updatable = false)
    private UsersEntity userEntity;

    // FK 컬럼(long) – 실제 DB 저장용
    @Column(name = "user_pk", nullable = false)
    private long userPk;


    @Column(name = "target_pk", nullable = false)
    private long targetPk;

    @Column(name = "invite_pk", nullable = false)
    private long invitePk;



}
