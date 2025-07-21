package com.owl.trade_market.service.impl;

import com.owl.trade_market.dto.UserDto;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.entity.UserDetails;
import com.owl.trade_market.repository.UserDetailsRepository;
import com.owl.trade_market.repository.UserRepository;
import com.owl.trade_market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(UserDto userDto) {
        // 중복 검사
        if (existsByUserId(userDto.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (existsByUserName(userDto.getUserName())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }

        // User 생성 및 저장
        User user = new User(userDto.getUserId(), userDto.getUserName(), passwordEncoder.encode(userDto.getUserPassword()));
        User savedUser = userRepository.save(user);

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
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateLocationByEmail(String email, String address) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
        user.setUserLocation(address);
        userRepository.save(user);
    }

    /**
     * UserDetails 생성 - 안정화된 버전
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDetails createUserDetails(User user) {
        try {
            // 입력 검증
            if (user == null || user.getId() == null) {
                throw new IllegalArgumentException("User 객체가 null이거나 ID가 없습니다.");
            }

            // 이미 존재하는지 확인
            Optional<UserDetails> existingDetails = userDetailsRepository.findByUserId(user.getId());
            if (existingDetails.isPresent()) {
                System.out.println("UserDetails 이미 존재함: User ID " + user.getId());
                return existingDetails.get();
            }

            // 최신 User 정보로 다시 조회 (detached 상태 방지)
            User managedUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User를 찾을 수 없습니다: " + user.getId()));

            // UserDetails 생성
            UserDetails userDetails = new UserDetails(managedUser);

            // 양방향 관계 설정
            managedUser.setUserDetails(userDetails);

            // UserDetails 저장
            UserDetails savedUserDetails = userDetailsRepository.save(userDetails);

            System.out.println("UserDetails 생성 및 저장 성공: User ID " + managedUser.getId());
            return savedUserDetails;

        } catch (Exception e) {
            System.err.println("UserDetails 생성 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("UserDetails 생성에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetails> findUserDetailsByUser(User user) {
        if (user == null || user.getId() == null) {
            return Optional.empty();
        }
        return userDetailsRepository.findByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetails> findUserDetailsByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return userDetailsRepository.findByUserId(userId);
    }

    /**
     * UserDetails 업데이트 - 안정화된 버전
     */
    @Override
    @Transactional
    public UserDetails updateUserDetails(UserDetails userDetails) {
        try {
            if (userDetails == null) {
                throw new IllegalArgumentException("UserDetails가 null입니다.");
            }

            if (userDetails.getUserId() == null) {
                throw new IllegalArgumentException("UserDetails의 userId가 null입니다.");
            }

            // 기존 UserDetails 조회
            UserDetails existingDetails = userDetailsRepository.findByUserId(userDetails.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("UserDetails를 찾을 수 없습니다: " + userDetails.getUserId()));

            // 필드 업데이트
            existingDetails.setIntroduction(userDetails.getIntroduction());
            existingDetails.setAge(userDetails.getAge());
            existingDetails.setGender(userDetails.getGender());

            return userDetailsRepository.save(existingDetails);

        } catch (Exception e) {
            System.err.println("UserDetails 업데이트 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("UserDetails 업데이트에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User 객체가 null이거나 ID가 없습니다.");
        }
        return userRepository.save(user);
    }

    /**
     * UserDetails 안전한 조회 또는 생성
     */
    @Override
    @Transactional
    public UserDetails getOrCreateUserDetails(User user) {
        try {
            // 먼저 조회 시도
            Optional<UserDetails> existingDetails = findUserDetailsByUserId(user.getId());
            if (existingDetails.isPresent()) {
                return existingDetails.get();
            }

            // 없으면 생성
            return createUserDetails(user);

        } catch (Exception e) {
            System.err.println("UserDetails 조회/생성 중 오류: " + e.getMessage());
            // 최후의 수단: 메모리상 객체 반환
            return new UserDetails(user);
        }
    }
}