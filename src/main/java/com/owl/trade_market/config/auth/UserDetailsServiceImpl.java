package com.owl.trade_market.config.auth;

import com.owl.trade_market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findByUserId(userId)
                //사용자가 존재하면 Spring Security의 User 객체로 변환하여 반환
                .map(user -> User.withUsername(user.getUserId())
                        .password(user.getUserPassword())
                        .roles("USER") // 모든 사용자에게 "USER" 역할을 부여 (추후 확장 가능)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다." + userId));

    }
}
