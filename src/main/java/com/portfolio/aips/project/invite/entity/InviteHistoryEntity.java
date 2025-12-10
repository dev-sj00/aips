package com.portfolio.aips.project.invite.entity;


import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = "invite_history")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class InviteHistoryEntity {

    @Id
    @Column(name = "invite_history_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pk;



    @Column(name = "invite_pk", insertable = false, updatable = false)
    private long invitePk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_pk")
    private InviteEntity inviteEntity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", insertable = false, updatable = false)
    private UsersEntity usersEntity; //과거 검색한 유저 엔티티





    @Column(name = "user_pk", nullable = false)
    private long userPk;



    @CreationTimestamp
    @Column(name = "created_date_time", nullable = false, updatable = false)
    private LocalDateTime createdAt;



}
