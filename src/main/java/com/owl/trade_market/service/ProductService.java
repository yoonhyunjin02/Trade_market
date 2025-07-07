package com.owl.trade_market.service;

import com.owl.trade_market.entity.Category;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {
    Product createProduct(String title, User seller, String description, int price, String location, Category category);
    Optional<Product> findById(Long id);
    Page<Product> findAll(Pageable pageable);

    Page<Product> searchProduct(String keyword, Category category, Pageable pageable);
}
