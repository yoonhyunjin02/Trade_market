package com.owl.trade_market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId // 중요: User의 ID를 UserDetails의 ID로 사용
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "age")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "manner_temperature", precision = 3, scale = 1)
    private BigDecimal mannerTemperature = new BigDecimal("36.5");

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 기본 생성자
    public UserDetails() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // User와 함께 생성하는 생성자 - 수정됨
    public UserDetails(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User 또는 User ID가 null입니다.");
        }
        this.user = user;
        // @MapsId를 사용하므로 userId는 자동으로 설정됨
        this.mannerTemperature = new BigDecimal("36.5");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // JPA 콜백 메서드
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        // @MapsId를 사용하므로 수동으로 userId 설정할 필요 없음
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public BigDecimal getMannerTemperature() {
        return mannerTemperature;
    }

    public void setMannerTemperature(BigDecimal mannerTemperature) {
        this.mannerTemperature = mannerTemperature;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "userId=" + userId +
                ", introduction='" + introduction + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", mannerTemperature=" + mannerTemperature +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}