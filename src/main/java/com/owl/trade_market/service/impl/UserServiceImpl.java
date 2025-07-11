package com.owl.trade_market.service.impl;

import com.owl.trade_market.config.exception.RegistrationException;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.dto.UserDto;
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

        User user = new User(userDto.getUserId(), userDto.getUserName(), passwordEncoder.encode(userDto.getUserPassword()));
        return userRepository.save(user);
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
}