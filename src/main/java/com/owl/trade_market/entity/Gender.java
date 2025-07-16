package com.owl.trade_market.entity;

public enum Gender {
    MALE("남"),
    FEMALE("여");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // 한글 텍스트로 Gender 찾기
    public static Gender fromDescription(String description) {
        for (Gender gender : Gender.values()) {
            if (gender.description.equals(description)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 성별입니다: " + description);
    }
}