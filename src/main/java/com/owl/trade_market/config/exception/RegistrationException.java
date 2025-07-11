package com.owl.trade_market.config.exception;

import com.owl.trade_market.dto.UserDto;

// RuntimeException을 상속받아 Unchecked Exception으로 만듭니다.
public class RegistrationException extends RuntimeException {
    
    private final UserDto userDto; // 사용자 입력값을 담을 필드

    public RegistrationException(String message, UserDto userDto) {
        super(message); // 예외 메시지는 부모 클래스에 전달
        this.userDto = userDto;
    }

    public UserDto getUserDto() {
        return userDto;
    }
}
