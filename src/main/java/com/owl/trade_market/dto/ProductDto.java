package com.owl.trade_market.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductDto {

    @NotBlank(message = "상품제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "상품설명은 필수입니다.")
    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다.")
    private String description;

    @NotNull(message = "상품가격은 필수입니다.")
    @Positive(message = "가격은 0보다 큰 값이어야 합니다.")
    private Integer  price= 0;

    // 카테고리명 추가 (사용자가 직접 입력)
    @Size(max = 50, message = "카테고리명은 50자를 초과할 수 없습니다.")
    private String categoryName;

    private String location;

    public ProductDto() {
    }

    public ProductDto(String title, String description, int price) {
        this.title = title;
        this.description = description;
        this.price = price;
    }

    // 카테고리명 포함 생성자 추가
    public ProductDto(String title, String description, int price, String categoryName, String location) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.categoryName = categoryName;
        this.location = location;
    }
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getLocation() {return location;}

    public void setLocation(String location) {this.location = location;}

    @Override
    public String toString() {
        return "ProductDto{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}