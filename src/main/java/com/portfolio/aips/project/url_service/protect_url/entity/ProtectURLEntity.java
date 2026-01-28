package com.portfolio.aips.project.url_service.protect_url.entity;

import com.portfolio.aips.project.invite.entity.InviteUserListEntity;
import com.portfolio.aips.project.url_service.common.entity.URLServiceBaseEntity;
import com.portfolio.aips.project.url_service.common.entity.URLStatusEntity;
import com.portfolio.aips.project.users.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "protect_url")
@Getter
@Setter

public class ProtectURLEntity extends URLServiceBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "protect_url_pk")
    private long pk;

    @Column(name ="url_password")
    private String urlPassword;


    //not set
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_pk")
    private UsersEntity writer;


    @Column(name = "user_pk", insertable=false, updatable=false)
    private Long writerPk;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InviteUserListEntity> inviteUserListEntity = new ArrayList<>();


    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL,  orphanRemoval = true)
    @JoinColumn(name = "url_status_pk", referencedColumnName = "url_status_pk")
    private URLStatusEntity urlStatusEntity;


    //생성에서는 saveAll, 그 이후 친구추가는 이 메서드 사용
    public void addProtectURLInvitedEntity(InviteUserListEntity protectURLInvitedEntity) {
        this.inviteUserListEntity.add(protectURLInvitedEntity);
    }


    public boolean isConfirmURLPassword(String urlPassword, String confirmUrlPassword) {
        return urlPassword.equals(confirmUrlPassword);
    }

}
