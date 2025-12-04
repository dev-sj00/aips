package com.portfolio.aips.project.url_service.protect_url.entity;

import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity(name = "protect_url_invited")
@Getter
@Setter
public class ProtectURLInvitedEntity {


    @Id
    @Column(name = "protect_url_invited_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tags_seq")
    @SequenceGenerator(
            name = "protect_url_invited_seq",
            sequenceName = "protect_url_invited_seq",
            allocationSize = 20   // Hibernate batch size와 맞춰주는 게 좋음
    )
    private Long pk;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk")
    private UsersEntity users;

    private long invitedUserPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protect_url_pk") // FK
    private ProtectURLEntity protectUrl;

    private long protectUrlPk;

}
