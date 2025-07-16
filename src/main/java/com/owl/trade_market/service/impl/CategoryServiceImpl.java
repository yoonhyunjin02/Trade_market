package com.owl.trade_market.service.impl;

import com.owl.trade_market.entity.Category;
import com.owl.trade_market.repository.CategoryRepository;
import com.owl.trade_market.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category findOrCreateCategory(String categoryName) {
        // null이나 빈 문자열 체크
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return null;
        }

        // 공백 제거 및 정규화
        String normalizedName = categoryName.trim();

        // 기존 카테고리 찾기
        Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(normalizedName);
        if (existingCategory.isPresent()) {
            return existingCategory.get();
        }

        // 새 카테고리 생성 시도 (중복 키 예외 처리)
        try {
            Category newCategory = new Category();
            newCategory.setName(normalizedName);
            newCategory.setCount(0); // 초기값 0
            return categoryRepository.save(newCategory);

        } catch (DataIntegrityViolationException e) {
            // 중복 키 예외 발생 시: 다시 조회 시도
            // 다른 트랜잭션에서 같은 이름의 카테고리를 생성했을 가능성
            Optional<Category> retryCategory = categoryRepository.findByNameIgnoreCase(normalizedName);
            if (retryCategory.isPresent()) {
                return retryCategory.get();
            }

            // 여전히 찾을 수 없으면 예외 재발생
            throw new RuntimeException("카테고리 생성 중 예상치 못한 오류가 발생했습니다: " + normalizedName, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return categoryRepository.findByNameIgnoreCase(name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public void updateCategoryCount(Category category) {
        if (category == null) return;

        // 해당 카테고리의 실제 상품 수를 계산
        int actualCount = category.getProducts().size();
        category.setCount(actualCount);
        categoryRepository.save(category);
    }

    @Override
    public void increaseCategoryCount(Category category) {
        if (category == null) return;

        category.setCount(category.getCount() + 1);
        categoryRepository.save(category);
    }

    @Override
    public void decreaseCategoryCount(Category category) {
        if (category == null) return;

        int newCount = Math.max(0, category.getCount() - 1);
        category.setCount(newCount);
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getPopularCategories() {
        return categoryRepository.findAllOrderByCountDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getPopularCategories(int limit) {
        List<Category> allCategories = categoryRepository.findAllOrderByCountDesc();
        return allCategories.stream()
                .limit(limit)
                .toList();
    }
}