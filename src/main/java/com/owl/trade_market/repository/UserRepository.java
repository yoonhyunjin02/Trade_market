package com.owl.trade_market.repository;

import com.owl.trade_market.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 아이디로 사용자 찾기 (로그인용)
    Optional<User> findByUserId(String userId);

    // 아이디 중복 확인 (회원가입용)
    boolean existsByUserId(String userId);

    // 사용자명(닉네임) 중복 확인 (회원가입용)
    boolean existsByUserName(String userName);

    Optional<User> findByUserEmail(String userEmail);

    boolean existsByUserEmail(String userEmail);

}
