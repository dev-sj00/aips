package com.portfolio.aips.project.invite.entity;


import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "favorite_invite_friend")
@Getter
@Setter
public class FavoriteInviteFriendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_invite_friend_pk")
    private Long pk;


    @Column(name = "invite_pk", nullable = false)
    private long invitePk;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", insertable = false, updatable = false)
    private UsersEntity usersEntity; //찜한 친구 유저


    @Column(name = "user_pk", nullable = false)
    private long userPk;


}
