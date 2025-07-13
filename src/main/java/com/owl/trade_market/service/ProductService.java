package com.owl.trade_market.service;

import com.owl.trade_market.entity.Category;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    // 기본 CRUD 메서드들
    Product createProduct(String title, User seller, String description, int price, String location, Category category);
    Optional<Product> findById(Long id);
    Page<Product> findAll(Pageable pageable);
    List<Product> findAll(Sort sort);

    // 상품 수정/삭제
    Product updateProduct(Long id, String title, String description, int price, String location, Category category);
    void deleteProduct(Long id);

    // 조회수 증가
    void increaseViewCount(Long id);

    // 카테고리 없이 검색어만 있을 때 검색
    Page<Product> searchProductByKeyword(String keyword, Pageable pageable);

    // 검색 관련
    Page<Product> searchProduct(String keyword, Category category, Pageable pageable);

    // 카테고리 관련
    Page<Product> findByCategory(Category category, Pageable pageable);
    List<Product> findByCategory(Category category, Sort sort);
}
