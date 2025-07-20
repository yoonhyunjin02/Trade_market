package com.owl.trade_market.service.impl;

import com.owl.trade_market.config.exception.RegistrationException;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.dto.UserDto;
import com.owl.trade_market.entity.UserDetails;
import com.owl.trade_market.repository.UserDetailsRepository;
import com.owl.trade_market.repository.UserRepository;
import com.owl.trade_market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Override
    @Transactional
    public User register(UserDto userDto) {
        if (existsByUserId(userDto.getUserId())) {
            throw new RegistrationException("이미 존재하는 아이디입니다.", userDto);
        }

        if (existsByUserName(userDto.getUserName())) {
            throw new RegistrationException("이미 존재하는 사용자명입니다.", userDto);
        }

        // User 생성 및 저장
        User user = new User(userDto.getUserId(), userDto.getUserName(), passwordEncoder.encode(userDto.getUserPassword()));
        User savedUser = userRepository.save(user);

        // 회원가입 시 UserDetails 생성은 지연 처리
        // (회원가입 즉시 생성하지 말고 프로필 접근 시 생성)
        System.out.println("회원가입 완료, UserDetails는 프로필 접근 시 생성됩니다: " + savedUser.getUserId());

        return savedUser;
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    @Transactional
    public void updateLocation(String userId, String address) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자는 찾을 수 없습니다: " + userId));
        user.setUserLocation(address);
    }

    @Override
    @Transactional
    public void updateLocationByEmail(String email, String address) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
        user.setUserLocation(address);
    }

    // UserDetails 관련 함수 - 단순화
    @Override
    @Transactional
    public UserDetails createUserDetails(User user) {
        // 입력 검증
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User 객체가 null이거나 ID가 없습니다.");
        }

        try {
            // 이미 UserDetails가 있는지 확인
            Optional<UserDetails> existingDetails = userDetailsRepository.findByUserId(user.getId());
            if (existingDetails.isPresent()) {
                System.out.println("UserDetails 이미 존재함: User ID " + user.getId());
                return existingDetails.get();
            }

            // UserDetails 생성 및 저장
            UserDetails userDetails = new UserDetails(user);
            UserDetails savedUserDetails = userDetailsRepository.save(userDetails);

            System.out.println("UserDetails 생성 성공: User ID " + user.getId());
            return savedUserDetails;

        } catch (Exception e) {
            System.err.println("UserDetails 생성 실패: " + e.getMessage());
            // 실패 시 기본 UserDetails 반환
            return createDefaultUserDetails(user);
        }
    }

    /**
     * 예외 상황에서 사용할 기본 UserDetails 생성 (DB 저장 없이)
     */
    private UserDetails createDefaultUserDetails(User user) {
        UserDetails defaultDetails = new UserDetails(user);
        System.out.println("기본 UserDetails 생성됨 (DB 저장 안됨): User ID " + user.getId());
        return defaultDetails;
    }

    @Override
    public Optional<UserDetails> findUserDetailsByUser(User user) {
        if (user == null || user.getId() == null) {
            return Optional.empty();
        }
        return userDetailsRepository.findByUser(user);
    }

    @Override
    public Optional<UserDetails> findUserDetailsByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return userDetailsRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public UserDetails updateUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("UserDetails가 null입니다.");
        }
        return userDetailsRepository.save(userDetails);
    }
}