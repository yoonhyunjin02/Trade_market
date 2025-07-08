package com.owl.trade_market.repository;

import com.owl.trade_market.entity.Category;
import com.owl.trade_market.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:category IS NULL OR p.category = :category) " +
            "ORDER BY p.createdAt DESC")
    Page<Product> findByKeywordAndCategory(@Param("keyword") String keyword,
                                           @Param("category") Category category,
                                           Pageable pageable);
}
