package com.owl.trade_market.service.impl;

import com.owl.trade_market.entity.User;
import com.owl.trade_market.dto.UserDto;
import com.owl.trade_market.repository.UserRepository;
import com.owl.trade_market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(UserDto userDto) {
        if (existsByUserId(userDto.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (existsByUserName(userDto.getUserName())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }

        if (!userDto.isPasswordMatching()) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        User user = new User(userDto.getUserId(), userDto.getUserName(), passwordEncoder.encode(userDto.getUserPassword()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(String userId, String userPassword) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isPresent() && passwordEncoder.matches(userPassword, user.get().getUserPassword())) {
            return user;
        }
        return Optional.empty();
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
}