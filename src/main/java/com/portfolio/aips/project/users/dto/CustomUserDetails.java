package com.portfolio.aips.project.users.dto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

@Getter

@Slf4j
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long userId;  // ✅ 추가 필드
    private final String provider;

    private final User user;    // ✅ 기존 User 객체를 감쌈



    // ✅ 정적 빌더 메서드
    public static CustomUserDetails build(Long userId, String provider, String password, String authorities) {


        String userName = "user";


        User springUser = (User) User.builder()
                .username(userName)
                .password(password)
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

        return new CustomUserDetails(userId, provider, springUser);
    }

    // === UserDetails 위임 ===


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { return user.isAccountNonExpired(); }

    @Override
    public boolean isAccountNonLocked() { return user.isAccountNonLocked(); }

    @Override
    public boolean isCredentialsNonExpired() { return user.isCredentialsNonExpired(); }

    @Override
    public boolean isEnabled() { return user.isEnabled(); }

    public Long getPk()
    {
        return this.userId;
    }

}
