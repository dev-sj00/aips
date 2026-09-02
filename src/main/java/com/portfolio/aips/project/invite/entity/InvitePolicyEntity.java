package com.portfolio.aips.project.invite.entity;

import com.portfolio.aips.project.invite.enums.InviteType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "invite_policy")
@Getter
@Setter
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "inviteCache")
public class InvitePolicyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invite_policy_pk")
    private Long pk;


    @Enumerated(EnumType.STRING)
    @Column(name = "target_type")
    private InviteType targetType;

    @Column(name="max_invite_count")
    private int maxInviteCount;


    @Column(name="max_favorite_count")
    private int maxFavoriteCount; // favoriteInviteFriends 최대

    @Column(name="max_history_count")
    private int maxHistoryCount;






}
