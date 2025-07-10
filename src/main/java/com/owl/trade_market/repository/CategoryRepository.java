package com.owl.trade_market.repository;

import com.owl.trade_market.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 카테고리명으로 조회 (대소문자 구분 없음)
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Category> findByNameIgnoreCase(@Param("name") String name);

    // 카테고리명 존재 여부 확인
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    // 상품 개수가 많은 순으로 카테고리 조회 (인기 카테고리)
    @Query("SELECT c FROM Category c ORDER BY c.count DESC")
    List<Category> findAllOrderByCountDesc();
}