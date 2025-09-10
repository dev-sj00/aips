package com.portfolio.aips.project.users.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "social_login_info")
public class SocialLoginInfo {


    @Column(name = "sc_pk")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;



    @OneToOne(mappedBy = "scInfo", optional = false)
    UsersEntity usersEntity;

    @Column(nullable = false)
    private String principalName;

    @Column(name = "provider", length = 50, nullable = false)
    private String provider;

    @Column(name = "refresh_token", length = 500, nullable = false)
    private String refreshToken;

    @Column(name = "access_token", length = 500, nullable = false)
    private String accessToken;


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

    public boolean isEqualExpired(Instant newExpired) {

        return expiresAt.equals(newExpired);
    }

    public boolean isValidAccessToken(String jwtAccessToken) {

        return jwtAccessToken != null && jwtAccessToken.equals(this.accessToken);
    }

}
