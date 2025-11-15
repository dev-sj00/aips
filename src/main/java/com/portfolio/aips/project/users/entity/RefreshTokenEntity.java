package com.portfolio.aips.project.users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "refresh_token")
public class RefreshTokenEntity {


    @Column(name = "rt_pk")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;


    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "refresh_token", length = 2000, nullable = false)
    private String refreshToken;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_pk")
    UsersEntity usersEntity;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public boolean isExpired() {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return true; // RefreshToken이 없으면 만료된 것으로 간주
        }

        if (expiresAt == null) {
            return false; // 만료시간이 설정되지 않았으면 만료되지 않은 것으로 간주
        }

        return Instant.now().isAfter(expiresAt);
    }

    public boolean isEquals(String refreshToken) {

        return refreshToken.equals(this.refreshToken);
    }



}
