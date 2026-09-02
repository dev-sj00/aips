package com.portfolio.aips.project.invite.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(
        name = "invite_history",
        uniqueConstraints = @UniqueConstraint(columnNames = {"owner_user_pk" , "target_user_pk", "invite_policy_pk"})
)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class InviteHistoryEntity extends BaseInviteEntity{

    @Id
    @Column(name = "invite_history_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pk;






    @CreationTimestamp
    @Column(name = "created_date_time", nullable = false, updatable = false)
    private LocalDateTime createdAt;





}
