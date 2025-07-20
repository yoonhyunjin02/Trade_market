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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        // 회원가입 시 즉시 UserDetails 생성
        try {
            createUserDetails(savedUser);
            System.out.println("회원가입 시 UserDetails 생성 성공: " + savedUser.getUserId());
        } catch (Exception e) {
            System.err.println("회원가입 시 UserDetails 생성 실패, 나중에 생성됨: " + e.getMessage());
            // 실패해도 회원가입은 완료되도록 함
        }

        return savedUser;
    }

    //Spring Security Login 사용으로 주석처리
    /*@Override
    public Optional<User> login(String userId, String userPassword) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isPresent() && passwordEncoder.matches(userPassword, user.get().getUserPassword())) {
            return user;
        }
        return Optional.empty();
    }*/

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


    // UserDetails 관련 함수
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDetails createUserDetails(User user) {
        try {
            // 이미 UserDetails가 있는지 다시 한 번 확인 (동시성 문제 방지)
            Optional<UserDetails> existingDetails = userDetailsRepository.findByUser(user);
            if (existingDetails.isPresent()) {
                System.out.println("UserDetails 이미 존재함: User ID " + user.getId());
                return existingDetails.get();
            }

            // User가 저장되어 ID가 있는지 확인
            if (user.getId() == null) {
                throw new IllegalStateException("User must be saved before creating UserDetails");
            }

            // @MapsId를 사용하는 올바른 방법
            UserDetails userDetails = new UserDetails(user); // 생성자 사용

            UserDetails savedUserDetails = userDetailsRepository.save(userDetails);
            System.out.println("UserDetails 생성 성공: User ID " + user.getId());
            return savedUserDetails;

        } catch (DataIntegrityViolationException e) {
            // 동시성 문제로 이미 생성된 경우, 다시 조회해서 반환
            System.out.println("동시성 문제로 UserDetails 생성 실패, 기존 것 조회: " + e.getMessage());
            Optional<UserDetails> existingDetails = userDetailsRepository.findByUser(user);
            if (existingDetails.isPresent()) {
                return existingDetails.get();
            } else {
                // 그래도 없으면 기본 UserDetails 반환
                return createDefaultUserDetails(user);
            }
        } catch (Exception e) {
            // 기타 예외 발생 시 기본 UserDetails 반환
            System.err.println("UserDetails 생성 실패: " + e.getMessage());
            return createDefaultUserDetails(user);
        }
    }

    /**
     * 예외 상황에서 사용할 기본 UserDetails 생성 (DB 저장 없이)
     */
    private UserDetails createDefaultUserDetails(User user) {
        UserDetails defaultDetails = new UserDetails(user); // 생성자 사용
        System.out.println("기본 UserDetails 생성됨 (DB 저장 안됨): User ID " + user.getId());
        return defaultDetails;
    }

    @Override
    public Optional<UserDetails> findUserDetailsByUser(User user) {
        return userDetailsRepository.findByUser(user);
    }

    @Override
    public Optional<UserDetails> findUserDetailsByUserId(Long userId) {
        return userDetailsRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public UserDetails updateUserDetails(UserDetails userDetails) {
        return userDetailsRepository.save(userDetails);
    }


}