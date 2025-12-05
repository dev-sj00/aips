package com.portfolio.aips.project.invite.entity;


import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;

@Entity(name = "invite_history")
public class InviteHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pk;



    @Column(name = "invite_pk", nullable = false)
    private long invitePk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", insertable = false, updatable = false)
    private UsersEntity usersEntity; //과거 추가한 엔티티




    @Column(name = "user_pk", nullable = false)
    private long userPk;



}
