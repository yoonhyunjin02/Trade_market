package com.owl.trade_market.service;

import com.owl.trade_market.entity.User;
import com.owl.trade_market.dto.UserDto;

import java.util.Optional;

public interface UserService {

    // 회원가입 (UserDto 사용)
    User register(UserDto userDto);

    // 로그인
//    Optional<User> login(String userId, String userPassword);

    // 사용자 찾기
    Optional<User> findByUserId(String userId);

    // 아이디 중복 확인
    boolean existsByUserId(String userId);

    // 사용자명 중복 확인
    boolean existsByUserName(String userName);

    void updateLocation(String username, String address);

    // 소셜 로그인용: 이메일로 위치 업데이트
    void updateLocationByEmail(String email, String address);
}

