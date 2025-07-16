package com.owl.trade_market.service;

import com.owl.trade_market.entity.User;
import com.owl.trade_market.dto.UserDto;
import com.owl.trade_market.entity.UserDetails;

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

    //UserDetails 관련
    //User와 함께 기본 UserDetails 생성

    UserDetails createUserDetails(User user);

    //UserDetails 조회 (User로)
    Optional<UserDetails> findUserDetailsByUser(User user);

    //UserDetails 조회 (userId로)
    Optional<UserDetails> findUserDetailsByUserId(Long userId);

    //UserDetails 업데이트
    UserDetails updateUserDetails(UserDetails userDetails);

}

