package com.owl.trade_market.repository;

import com.owl.trade_market.entity.Category;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 검색
    @Query("""
    SELECT p FROM Product p
    WHERE 
        (:keyword IS NULL OR 
         LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
         LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
         LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND 
        (:category IS NULL OR p.category = :category)
    ORDER BY p.createdAt DESC
    """)
    Page<Product> search(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            Pageable pageable
    );

    // 검색어만 있을 때
    Page<Product> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);


    // 카테고리별 조회
    Page<Product> findByCategory(Category category, Pageable pageable);
    List<Product> findByCategory(Category category, Sort sort);

    // 판매자별 조회 (마이페이지용)
    List<Product> findBySeller(User seller, Sort sort);
}