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
import java.util.Optional;

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
    
    // 위치 조회
    @Query("SELECT DISTINCT p.location FROM Product p WHERE p.location IS NOT NULL AND p.location <> ''")
    List<String> findDistinctLocations();

    // 판매 중인 상품 조회
    Page<Product> findBySoldFalse(Pageable pageable);

    // 카테고리별 + 판매중 조회
    Page<Product> findByCategoryAndSoldFalse(Category category, Pageable pageable);

    // 검색 + 거래가능만 보기 (soldOrNot = false)
    @Query("""
    SELECT p FROM Product p
    WHERE p.sold = false
      AND (:category IS NULL OR p.category = :category)
      AND (
          LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
          LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
          LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    ORDER BY p.createdAt DESC
    """)
    Page<Product> searchByKeywordAndSoldFalse(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            Pageable pageable
    );

    // 최솟값/최댓값
    @Query("SELECT MIN(p.price) FROM Product p")
    Optional<Integer> findMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    Optional<Integer> findMaxPrice();

    // 가격 구간 필터링
    Page<Product> findByPriceBetween(Integer minPrice, Integer maxPrice, Pageable pageable);
    Page<Product> findBySoldFalseAndPriceBetween(Integer minPrice, Integer maxPrice, Pageable pageable);
    Page<Product> findByCategoryAndPriceBetween(Category category, Integer minPrice, Integer maxPrice, Pageable pageable);
    Page<Product> findByCategoryAndSoldFalseAndPriceBetween(Category category, Integer minPrice, Integer maxPrice, Pageable pageable);

    // 검색 + 가격
    @Query("""
    SELECT p FROM Product p
    WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:category IS NULL OR p.category = :category)
      AND p.price BETWEEN :minPrice AND :maxPrice
    """)
    Page<Product> searchKeywordCategoryWithPrice(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Product p
    WHERE p.sold = false
      AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:category IS NULL OR p.category = :category)
      AND p.price BETWEEN :minPrice AND :maxPrice
    """)
    Page<Product> searchKeywordCategorySoldFalseWithPrice(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Product p
    WHERE p.price BETWEEN :minPrice AND :maxPrice
      AND (:location IS NULL OR p.location = :location)
      AND (:availableOnly IS NULL OR p.sold = false)
    """)
    Page<Product> findAllWithPriceAndLocation(
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("location") String location,
            @Param("availableOnly") Boolean availableOnly,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Product p
    WHERE p.category = :category
      AND p.price BETWEEN :minPrice AND :maxPrice
      AND (:location IS NULL OR p.location = :location)
      AND (:availableOnly IS NULL OR p.sold = false)
    """)
    Page<Product> findByCategoryWithPriceAndLocation(
            @Param("category") Category category,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("location") String location,
            @Param("availableOnly") Boolean availableOnly,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Product p
    WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:category IS NULL OR p.category = :category)
      AND p.price BETWEEN :minPrice AND :maxPrice
      AND (:location IS NULL OR p.location = :location)
      AND (:availableOnly IS NULL OR p.sold = false)
    """)
    Page<Product> searchWithPriceAndLocationFilter(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("location") String location,
            @Param("availableOnly") Boolean availableOnly,
            Pageable pageable
    );
}