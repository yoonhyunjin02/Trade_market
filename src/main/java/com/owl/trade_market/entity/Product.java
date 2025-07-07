package com.owl.trade_market.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(nullable = false, name = "product_title")
    private String title;

    @Column(columnDefinition = "TEXT", name = "product_description")
    private String description;

    @Column(nullable = false, name = "product_price")
    private int price;

    @Column(name = "product_location")
    private String location;

    @Column(name = "product_view_count")
    private int viewCount;

    @Column(name = "product_chat_count")
    private int chatCount;

    @Column(name = "product_sold_or_not")
    private boolean soldOrNot;

    @Column(name = "product_created_at")
    private LocalDateTime createdAt;

    public Product() {
    }

    public Product(Long id, User seller, String title, String description, int price, String location, int viewCount, int chatCount, boolean soldOrNot, LocalDateTime createdAt) {
        this.id = id;
        this.seller = seller;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.viewCount = viewCount;
        this.chatCount = chatCount;
        this.soldOrNot = soldOrNot;
        this.createdAt = createdAt;
    }

    // Business methods


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getChatCount() {
        return chatCount;
    }

    public void setChatCount(int chatCount) {
        this.chatCount = chatCount;
    }

    public boolean isSoldOrNot() {
        return soldOrNot;
    }

    public void setSoldOrNot(boolean soldOrNot) {
        this.soldOrNot = soldOrNot;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
