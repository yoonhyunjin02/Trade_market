package com.owl.trade_market.service.impl;

import com.owl.trade_market.entity.Category;
import com.owl.trade_market.entity.Image;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.repository.ProductRepository;
import com.owl.trade_market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product createProduct(String title, User seller, String description, int price, String location, Category category) {
        // 상품 생성
        Product product = new Product(seller, title, description, price, location, category);

        // 상품을 먼저 저장
        Product savedProduct = productRepository.save(product);

        // 기본 이미지 엔티티 생성 및 추가
        Image defaultImage = new Image(savedProduct, "/images/default-product.jpg");
        savedProduct.getImages().add(defaultImage);

        // 변경사항 저장 (cascade 옵션으로 Image도 함께 저장됨)
        return productRepository.save(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll(Sort sort) {
        return productRepository.findAll(sort);
    }

    @Override
    public Product updateProduct(Long id, String title, String description, int price, String location, Category category) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setTitle(title);
            product.setDescription(description);
            product.setPrice(price);
            product.setLocation(location);
            product.setCategory(category);
            return productRepository.save(product);
        }
        throw new IllegalArgumentException("Product not found with id: " + id);
    }

    @Override
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
    }

    @Override
    public void increaseViewCount(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setViewCount(product.getViewCount() + 1);
            productRepository.save(product);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProduct(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        return productRepository.search(keyword.trim(), pageable);
    }

    @Override
    public List<String> getAllDistinctLocations() {
        return productRepository.findDistinctLocations();
    }

    @Override
    public Integer findMinPrice() {
        return productRepository.findMinPrice().orElse(0);
    }

    @Override
    public Integer findMaxPrice() {
        return productRepository.findMaxPrice().orElse(1000000); // 없으면 기본 큰값
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> filterProducts(String keyword,
                                        Category category,
                                        Integer minPrice,
                                        Integer maxPrice,
                                        String location,
                                        Boolean availableOnly,
                                        Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return productRepository.filterProducts(
                kw, category, minPrice, maxPrice, location, availableOnly, pageable
        );
    }
}