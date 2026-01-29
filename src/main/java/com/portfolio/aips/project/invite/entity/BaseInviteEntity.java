package com.portfolio.aips.project.invite.entity;

import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseInviteEntity {
    @Column(name = "invite_policy_pk")
    private long invitePolicyPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_policy_pk", insertable = false, updatable = false)
    private InvitePolicyEntity invitePolicyEntity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_pk", insertable = false, updatable = false)
    private UsersEntity targetUsersEntity; //과거 검색한 유저 엔티티

    @Column(name = "target_user_pk", nullable = false)
    private long targetUserPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_user_pk", insertable=false, updatable=false)
    private UsersEntity ownerUsersEntity;

    @Column(name="owner_user_pk", nullable = false)
    private long ownerUserPk;

}
