package com.portfolio.aips.project.invite.entity;

import com.portfolio.aips.project.invite.enums.InviteType;
import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "invite")
@Getter
@Setter
public class InviteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invite_pk")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_pk", insertable=false, updatable=false)
    private UsersEntity owner;

    @Column(name="user_pk")
    private long ownerUserPk;

    @OneToMany(mappedBy = "inviteEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InviteHistoryEntity> inviteHistory = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_pk")
    private List<FavoriteInviteFriendEntity> favoriteInviteFriends = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_pk")
    private List<InviteUserListEntity> inviteUserLists = new ArrayList<>();


    public void addFavoriteInviteFriend(FavoriteInviteFriendEntity favoriteInviteFriend) {
        this.favoriteInviteFriends.add(favoriteInviteFriend);

    }

    public void addInviteHistory(InviteHistoryEntity inviteHistoryEntity) {
        this.inviteHistory.add(inviteHistoryEntity);
        inviteHistoryEntity.setInviteEntity(this);
    }




}
