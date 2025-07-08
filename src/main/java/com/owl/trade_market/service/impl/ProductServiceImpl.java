package com.owl.trade_market.service.impl;

import com.owl.trade_market.entity.Category;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.repository.ProductRepository;
import com.owl.trade_market.repository.UserRepository;
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
    public Page<Product> searchProduct(String keyword, Category category, Pageable pageable) {
        if (category != null) {
            return productRepository.findByKeywordAndCategory(keyword, category, pageable);
        } else {
            return productRepository.findByKeyword(keyword, pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategory(Category category, Sort sort) {
        return productRepository.findByCategory(category, sort);
    }
}