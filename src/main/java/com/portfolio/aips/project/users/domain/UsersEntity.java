package com.portfolio.aips.project.users.domain;

import com.portfolio.aips.project.users.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UsersEntity {

    @Column(name = "user_pk")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;


    @Column(nullable = false)
    private String nickname;


    private String profileImageUrl;




    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @PrePersist
    public void setDefaultRole() {
        if (role == null) {
            role = UserRole.ROLE_USER; // 기본값 설정
        }
    }


    @CreatedDate
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    private LocalDateTime updatedDateTime;


    @OneToOne
    @JoinColumn(name = "sc_info")
    private SocialLoginInfo scInfo;




}
