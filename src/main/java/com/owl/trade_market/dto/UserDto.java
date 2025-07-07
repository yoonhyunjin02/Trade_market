package com.owl.trade_market.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {
    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 4, max = 20, message = "아이디는 영어와 숫자 조합으로 4-20자여야 합니다")
    private String userId;

    @NotBlank(message = "닉네임은 필수입니다")
    private String userName;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    private String userPassword;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String confirmPassword;

    private String userLocation;

    // 생성자
    public UserDto() {}

    public UserDto(String userId, String userName, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    // 비밀번호 확인 일치 여부 체크
    public boolean isPasswordMatching() {
        return userPassword != null && userPassword.equals(confirmPassword);
    }

    // Getters & Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userLocation='" + userLocation + '\'' +
                '}';
    }
}
