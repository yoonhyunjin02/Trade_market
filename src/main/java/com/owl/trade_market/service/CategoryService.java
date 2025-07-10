package com.owl.trade_market.service;

import com.owl.trade_market.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    // 카테고리명으로 조회 또는 생성 (핵심 메서드)
    Category findOrCreateCategory(String categoryName);

    // 기본 CRUD
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
    List<Category> findAll();

    // 카테고리 상품 수 업데이트
    void updateCategoryCount(Category category);
    void increaseCategoryCount(Category category);
    void decreaseCategoryCount(Category category);

    // 인기 카테고리 조회
    List<Category> getPopularCategories();
    List<Category> getPopularCategories(int limit);
}