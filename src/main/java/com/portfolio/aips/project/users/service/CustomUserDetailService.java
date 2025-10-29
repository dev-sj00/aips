package com.portfolio.aips.project.users.service;

import com.portfolio.aips.project.users.domain.UsersEntity;
import com.portfolio.aips.project.users.enums.UserRole;
import com.portfolio.aips.project.users.repo.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService{
    private final UsersRepository usersRepository;



    // 소셜 로그인에서 사용 안함
    @Transactional
    public UserDetails loadUserByPrincipalNameAndProvider(String principalName, String provider) throws UsernameNotFoundException {

        UsersEntity usersEntity = usersRepository.findByPrincipalNameAndProvider(principalName, provider)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with principalName: " + principalName + " and provider: " + provider));


            return createUserDetails(usersEntity);
    }

    public UserDetails loadSocialUserByPrincipalNameAndProvider(String principalName, String provider) throws UsernameNotFoundException {

        return createUserDetails(principalName, provider);

    }




    // 자동 로그인 용
    private UserDetails createUserDetails(UsersEntity user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getPrincipalName())
                .password("") 
                .authorities(user.getRole().toString())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }

    
    //처음 로그인 용
    private UserDetails createUserDetails(String principalName, String provider) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(principalName)
                .password("") // OAuth2 사용자는 비밀번호가 없음
                .authorities(UserRole.ROLE_USER.toString()) //admin 계정은 자체 로그인만 가능
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }



}
