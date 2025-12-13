package com.portfolio.aips.project.invite.entity;

import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity(name = "invited_user_list")
@Getter
@Setter
public class InviteUserListEntity extends BaseInviteEntity{


    @Id
    @Column(name = "invited_user_list_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;



}
