package com.owl.trade_market.repository;

import com.owl.trade_market.entity.User;
import com.owl.trade_market.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

    // User 엔티티로 UserDetails 조회
    Optional<UserDetails> findByUser(User user);

    // userId로 UserDetails 조회
    Optional<UserDetails> findByUserId(Long userId);

    // UserDetails 존재 여부 확인
    boolean existsByUserId(Long userId);
}