package com.portfolio.aips.project.invite.entity;


import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "favorite_invite_friend")
@Getter
@Setter

public class FavoriteInviteFriendEntity extends  BaseInviteEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_invite_friend_pk")
    private long pk;




}
