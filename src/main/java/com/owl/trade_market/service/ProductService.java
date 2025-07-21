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

    // 검색(키워드)
    Page<Product> searchProduct(String keyword, Pageable pageable);

    // 중복 없는 위치 전체 조회
    List<String> getAllDistinctLocations();

//    // 판매중인 상품 조회
//    Page<Product> findAll(Pageable pageable, Boolean availableOnly);
//
//    Page<Product> findByCategoryAndAvailable(Category category, Pageable pageable);
//
//    Page<Product> searchProductAndAvailable(String keyword, Category category, Pageable pageable);

    // 판매자별 상품 조회 (프로필 페이지용)
    List<Product> findBySeller(User seller, Sort sort);

    // 전체 상품 중 최솟값 / 최댓값 구하기
    Integer findMinPrice();
    Integer findMaxPrice();
    
    // 필터
    Page<Product> filterProducts(String keyword,
                                 Category category,
                                 Integer minPrice,
                                 Integer maxPrice,
                                 String location,
                                 Boolean availableOnly,
                                 Pageable pageable);

    // 상품을 판매완료 상태로 변경
    void markAsSold(Long productId);
}
