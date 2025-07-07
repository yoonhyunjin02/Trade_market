package com.owl.trade_market.repository;

import com.owl.trade_market.entity.Category;
import com.owl.trade_market.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByKeywordAndCategory(@Param("keyword") String keyword,
                                           @Param("category") Category category,
                                           Pageable pageable);
}
