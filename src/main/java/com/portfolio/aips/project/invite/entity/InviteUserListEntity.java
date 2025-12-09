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

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", insertable = false, updatable = false)
    private UsersEntity userEntity;

    // FK 컬럼(long) – 실제 DB 저장용
    @Column(name = "user_pk", nullable = false)
    private long userPk;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_pk", insertable = false, updatable = false)
    private UsersEntity targetUserEntity;

    @Column(name = "target_pk", nullable = false)
    private long targetPk;

    @Column(name = "invite_pk", nullable = false)
    private long invitePk;



}
