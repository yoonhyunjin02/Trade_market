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
    WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
    ORDER BY p.createdAt DESC
    """)
    Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

    // 위치 조회
    @Query("SELECT DISTINCT p.location FROM Product p WHERE p.location IS NOT NULL AND p.location <> ''")
    List<String> findDistinctLocations();

    // 최솟값/최댓값
    @Query("SELECT MIN(p.price) FROM Product p")
    Optional<Integer> findMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    Optional<Integer> findMaxPrice();

    // trade 페이지 필터바
    @Query("""
    SELECT p FROM Product p
    WHERE
        (:keyword IS NULL OR :keyword = '' OR
            LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    AND (:category IS NULL OR p.category = :category)
    AND (:minPrice IS NULL OR p.price >= :minPrice)
    AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    AND (:location IS NULL OR p.location = :location)
    AND (:availableOnly IS NULL OR p.sold = false)
    """)
    Page<Product> filterProducts(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("location") String location,
            @Param("availableOnly") Boolean availableOnly,
            Pageable pageable
    );

    @Query("""
    SELECT DISTINCT p FROM Product p
    LEFT JOIN FETCH p.images
    WHERE
        (:keyword IS NULL OR :keyword = '' OR
            LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    AND (:category IS NULL OR p.category = :category)
    AND (:minPrice IS NULL OR p.price >= :minPrice)
    AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    AND (:location IS NULL OR p.location = :location)
    AND (:availableOnly IS NULL OR p.sold = false)
    """)
    Page<Product> filterProductsWithImages(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("location") String location,
            @Param("availableOnly") Boolean availableOnly,
            Pageable pageable
    );

    // 판매자별 상품 조회 (프로필 페이지용)
    List<Product> findBySeller(User seller, Sort sort);
    List<Product> findBySeller(User seller);

    @Query("""
    SELECT p FROM Product p
    LEFT JOIN FETCH p.images
    WHERE p.id = :id
""")
    Optional<Product> findByIdWithImages(@Param("id") Long id);
}