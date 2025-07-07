package com.owl.trade_market.service.impl;

import com.owl.trade_market.entity.Category;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.repository.ProductRepository;
import com.owl.trade_market.repository.UserRepository;
import com.owl.trade_market.service.ProductService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class ProductServiceImpl implements ProductService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product createProduct(String title, User seller, String description, int price, String location, Category category) {
        Product product = new Product(seller, title, description, price, location, category);
        return productRepository.save(product);
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
    public Page<Product> searchProduct(String keyword, Category category, Pageable pageable) {
        return productRepository.findByKeywordAndCategory(keyword, category, pageable);
    }

}
