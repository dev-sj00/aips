package com.portfolio.aips.project.users.domain;

import com.portfolio.aips.project.users.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "users")
public class UsersEntity {

    @Column(name = "user_pk")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;


    @Column(nullable = false)
    private String nickname;


    @Column(name="principal_name", nullable = false)
    private String principalName;

    @Column(name = "provider", length = 50, nullable = false)
    private String provider;


    @Column(name = "profile_image_url")
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
    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(name = "updated_date_time")
    private LocalDateTime updatedDateTime;


    @OneToMany(mappedBy = "usersEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default //빌더 패턴 으로 entity 생성 시 new ArrayList 할당 안됨
    private List<RefreshTokenEntity> refreshTokenEntity = new ArrayList<>();






    public void addRefreshToken(RefreshTokenEntity refreshTokenEntity) {

        log.info("refreshTokenEntity : {} ", refreshTokenEntity);
        this.refreshTokenEntity.add(refreshTokenEntity);
        refreshTokenEntity.setUsersEntity(this);
    }




}
