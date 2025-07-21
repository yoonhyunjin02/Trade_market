package com.owl.trade_market.dto;

import com.owl.trade_market.entity.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class ProfileEditDto {

    @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
    private String userName;

    @Size(max = 500, message = "자기소개는 500자 이하로 입력해주세요.")
    private String introduction;

    @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
    @Max(value = 150, message = "나이는 150 이하여야 합니다.")
    private Integer age;

    private String gender; // "MALE", "FEMALE", 또는 null

    @Size(max = 255, message = "위치는 255자 이하로 입력해주세요.")
    private String userLocation;

    // 기본 생성자
    public ProfileEditDto() {}

    // 생성자
    public ProfileEditDto(String userName, String introduction, Integer age, Gender gender, String userLocation) {
        this.userName = userName;
        this.introduction = introduction;
        this.age = age;
        this.gender = gender != null ? gender.name() : null;
        this.userLocation = userLocation;
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    // Gender enum으로 변환하는 헬퍼 메서드
    public Gender getGenderEnum() {
        if (gender == null || gender.trim().isEmpty()) {
            return null;
        }
        try {
            return Gender.valueOf(gender);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "ProfileEditDto{" +
                "userName='" + userName + '\'' +
                ", introduction='" + introduction + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", userLocation='" + userLocation + '\'' +
                '}';
    }
}