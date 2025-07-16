package com.owl.trade_market.service.impl;

import com.owl.trade_market.config.exception.RegistrationException;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.dto.UserDto;
import com.owl.trade_market.entity.UserDetails;
import com.owl.trade_market.repository.UserDetailsRepository;
import com.owl.trade_market.repository.UserRepository;
import com.owl.trade_market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
            // RegistrationException을 사용합니다.
            // 에러 메시지와 함께 userDto를 넘겨줍니다.
            throw new RegistrationException("이미 존재하는 아이디입니다.", userDto);
        }

        if (existsByUserName(userDto.getUserName())) {
            throw new RegistrationException("이미 존재하는 사용자명입니다.", userDto);
        }

        //user 생성
        User user = new User(userDto.getUserId(), userDto.getUserName(), passwordEncoder.encode(userDto.getUserPassword()));
        User savedUser = userRepository.save(user);

        // UserDetails 자동 생성 추가
        createUserDetails(savedUser);

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


    //UserDetails관련 함수
    @Override
    @Transactional
    public UserDetails createUserDetails(User user) {
        // 이미 UserDetails가 있는지 확인
        Optional<UserDetails> existingDetails = userDetailsRepository.findByUser(user);
        if (existingDetails.isPresent()) {
            return existingDetails.get();
        }

        // 새로운 UserDetails 생성
        UserDetails userDetails = new UserDetails(user);

        // 양방향 관계 설정
        user.setUserDetails(userDetails);

        return userDetailsRepository.save(userDetails);
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