package com.portfolio.aips.project.users.service;

import com.portfolio.aips.project.users.domain.UsersEntity;
import com.portfolio.aips.project.users.repo.UsersRepository;
import com.portfolio.aips.project.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UsersRepository usersRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    // Email과 Provider로 사용자 로드하는 커스텀 메서드
    public UserDetails loadUserByPrincipalNameAndProvider(String principalName, String provider) throws UsernameNotFoundException {

        UsersEntity usersEntity = usersRepository.findByScInfo_PrincipalNameAndScInfo_Provider(principalName, provider)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with principalName: " + principalName + " and provider: " + provider));


            return createUserDetails(usersEntity);


    }


    // UserDetails 객체 생성
    private UserDetails createUserDetails(UsersEntity user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getScInfo().getPrincipalName())
                .password("") // OAuth2 사용자는 비밀번호가 없음
                .authorities(user.getRole().toString())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }



}
