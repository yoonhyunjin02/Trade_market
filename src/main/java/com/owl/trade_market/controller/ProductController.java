package com.owl.trade_market.controller;

import com.owl.trade_market.dto.ProductDto;
import com.owl.trade_market.entity.Category;
import com.owl.trade_market.entity.Image;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.service.CategoryService;
import com.owl.trade_market.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    //상품 목록 페이지 (trade.html)
    @GetMapping
    public String productList(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Long categoryId,
                              Model model,
                              HttpSession session,
                              @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getCurrentUser(session, oauth2User);
        model.addAttribute("user", user);

        try {
            List<Product> allProducts;
            Category selectedCategory = null;

            // 카테고리 필터가 있는 경우
            if (categoryId != null) {
                Optional<Category> categoryOpt = categoryService.findById(categoryId);
                if (categoryOpt.isPresent()) {
                    selectedCategory = categoryOpt.get();
                }
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                // 키워드 + 카테고리 검색
                Page<Product> searchPage = productService.searchProduct(
                        keyword.trim(), selectedCategory,
                        Pageable.unpaged(Sort.by("createdAt").descending())
                );
                allProducts = searchPage.getContent();
            } else if (selectedCategory != null) {
                // 카테고리만 필터링
                allProducts = productService.findByCategory(selectedCategory, Sort.by("createdAt").descending());
            } else {
                // 전체 상품 조회
                allProducts = productService.findAll(Sort.by("createdAt").descending());
            }

            model.addAttribute("products", allProducts);
            model.addAttribute("keyword", keyword);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("selectedCategory", selectedCategory);

            // 인기 카테고리 목록 추가 (필터용)
            List<Category> popularCategories = categoryService.getPopularCategories(10);
            model.addAttribute("popularCategories", popularCategories);

        } catch (Exception e) {
            model.addAttribute("error", "상품 목록을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("products", java.util.Collections.emptyList());
        }

        return "pages/trade";
    }

    //검색 페이지 (search.html)
    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword,
                                 @RequestParam(required = false) Long categoryId,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model,
                                 HttpSession session,
                                 @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getCurrentUser(session, oauth2User);
        model.addAttribute("user", user);

        try {
            // 검색어 유효성 검사
            if (keyword == null || keyword.trim().isEmpty()) {
                model.addAttribute("error", "검색어를 입력해주세요.");
                model.addAttribute("products", java.util.Collections.emptyList());
                return "pages/search";
            }

            // 카테고리 조회
            Category category = null;
            if (categoryId != null) {
                category = categoryService.findById(categoryId).orElse(null);
            }

            // 페이징 처리
            Pageable pageable = PageRequest.of(page, 8, Sort.by("createdAt").descending());

            // 검색 실행
            Page<Product> searchPage = productService.searchProduct(keyword.trim(), category, pageable);

            // 모델 속성 등록
            model.addAttribute("page", searchPage);
            model.addAttribute("products", searchPage.getContent());
            model.addAttribute("keyword", keyword);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("selectedCategory", category);

            // 인기 카테고리 추가
            List<Category> popularCategories = categoryService.getPopularCategories(10);
            model.addAttribute("popularCategories", popularCategories);

        } catch (Exception e) {
            model.addAttribute("error", "검색 중 오류가 발생했습니다.");
            model.addAttribute("products", java.util.Collections.emptyList());
        }

        return "pages/search";
    }

    //카테고리별 상품 조회
    @GetMapping("/category/{categoryId}")
    public String productsByCategory(@PathVariable Long categoryId,
                                     Model model,
                                     HttpSession session,
                                     @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getCurrentUser(session, oauth2User);
        model.addAttribute("user", user);

        try {
            // 카테고리 조회
            Optional<Category> categoryOpt = categoryService.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                model.addAttribute("error", "존재하지 않는 카테고리입니다.");
                model.addAttribute("products", java.util.Collections.emptyList());
                return "pages/trade";
            }

            Category category = categoryOpt.get();

            // 해당 카테고리 상품 조회
            List<Product> categoryProducts = productService.findByCategory(category, Sort.by("createdAt").descending());

            model.addAttribute("products", categoryProducts);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("selectedCategory", category);

            // 인기 카테고리 목록 추가
            List<Category> popularCategories = categoryService.getPopularCategories(10);
            model.addAttribute("popularCategories", popularCategories);

        } catch (Exception e) {
            model.addAttribute("error", "카테고리 상품을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("products", java.util.Collections.emptyList());
        }

        return "pages/trade";
    }

    //상품 등록 폼 페이지 (write.html)
    @GetMapping("/new")
    public String writeForm(Model model,
                            HttpSession session,
                            @AuthenticationPrincipal OAuth2User oauth2User,
                            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session, oauth2User);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/users/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("productDto", new ProductDto());

        return "pages/write";
    }

    //상품 등록 처리
    @PostMapping("/new")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto,
                                BindingResult result,
                                HttpSession session,
                                @AuthenticationPrincipal OAuth2User oauth2User,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        User user = getCurrentUser(session, oauth2User);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/users/login";
        }

        // 유효성 검사 실패시
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("productDto", productDto);

            // 인기 카테고리 목록 다시 설정
            List<Category> popularCategories = categoryService.getPopularCategories(20);
            model.addAttribute("popularCategories", popularCategories);
            return "pages/write";
        }

        try {
            // 추가 유효성 검사
            if (productDto.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "상품 제목을 입력해주세요.");
                return "redirect:/products/new";
            }

            if (productDto.getDescription().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "상품 설명을 입력해주세요.");
                return "redirect:/products/new";
            }

            if (productDto.getPrice() <= 0) {
                redirectAttributes.addFlashAttribute("error", "올바른 가격을 입력해주세요.");
                return "redirect:/products/new";
            }

            // 카테고리 처리 (사용자가 입력한 카테고리명으로 찾기/생성)
            Category category = null;
            if (productDto.getCategoryName() != null && !productDto.getCategoryName().trim().isEmpty()) {
                category = categoryService.findOrCreateCategory(productDto.getCategoryName().trim());
            }

            // 상품 생성
            Product product = productService.createProduct(
                    productDto.getTitle().trim(),
                    user,
                    productDto.getDescription().trim(),
                    productDto.getPrice(),
                    user.getUserLocation(),
                    category
            );

            // 카테고리 상품 수 증가
            if (category != null) {
                categoryService.increaseCategoryCount(category);
            }

            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 등록되었습니다.");
            return "redirect:/products/" + product.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 등록 중 오류가 발생했습니다.");
            return "redirect:/products/new";
        }
    }

    //상품상세 페이지 (trade_post.html)
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id,
                                Model model,
                                HttpSession session,
                                @AuthenticationPrincipal OAuth2User oauth2User,
                                RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session, oauth2User);
        model.addAttribute("user", user);

        try {
            Optional<Product> productOpt = productService.findById(id);

            if (productOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "존재하지 않는 상품입니다.");
                return "redirect:/products";
            }

            Product product = productOpt.get();

            // 조회수 증가
            productService.increaseViewCount(id);

            model.addAttribute("product", product);

            // 현재 사용자가 판매자인지 확인
            boolean isOwner = user != null &&
                    user.getId().equals(product.getSeller().getId());
            model.addAttribute("isOwner", isOwner);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/products";
        }

        return "pages/trade_post";
    }

    //상품 수정 폼 페이지
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Model model,
                           HttpSession session,
                           @AuthenticationPrincipal OAuth2User oauth2User,
                           RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session, oauth2User);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/users/login";
        }

        try {
            Optional<Product> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "존재하지 않는 상품입니다.");
                return "redirect:/products";
            }

            Product product = productOpt.get();

            // 소유자 체크
            if (!product.getSeller().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
                return "redirect:/products/" + id;
            }

            // ProductDto로 변환 (카테고리명 포함)
            String categoryName = product.getCategory() != null ? product.getCategory().getName() : "";
            ProductDto productDto = new ProductDto(
                    product.getTitle(),
                    product.getDescription(),
                    product.getPrice(),
                    categoryName
            );

            model.addAttribute("user", user);
            model.addAttribute("productDto", productDto);
            model.addAttribute("productId", id);

            // 인기 카테고리 목록 추가
            List<Category> popularCategories = categoryService.getPopularCategories(20);
            model.addAttribute("popularCategories", popularCategories);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/products/" + id;
        }

        return "pages/write"; // 같은 폼 재사용
    }

    //상품 수정 처리
    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute ProductDto productDto,
                                BindingResult result,
                                HttpSession session,
                                @AuthenticationPrincipal OAuth2User oauth2User,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        User user = getCurrentUser(session, oauth2User);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/users/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("productDto", productDto);
            model.addAttribute("productId", id);

            // 인기 카테고리 목록 다시 설정
            List<Category> popularCategories = categoryService.getPopularCategories(20);
            model.addAttribute("popularCategories", popularCategories);
            return "pages/write";
        }

        try {
            Optional<Product> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "존재하지 않는 상품입니다.");
                return "redirect:/products";
            }

            Product product = productOpt.get();

            // 소유자 체크
            if (!product.getSeller().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
                return "redirect:/products/" + id;
            }

            // 기존 카테고리 저장 (카운트 조정용)
            Category oldCategory = product.getCategory();

            // 새 카테고리 처리
            Category newCategory = null;
            if (productDto.getCategoryName() != null && !productDto.getCategoryName().trim().isEmpty()) {
                newCategory = categoryService.findOrCreateCategory(productDto.getCategoryName().trim());
            }

            // 상품 정보 업데이트
            Product updatedProduct = productService.updateProduct(
                    id,
                    productDto.getTitle().trim(),
                    productDto.getDescription().trim(),
                    productDto.getPrice(),
                    user.getUserLocation(),
                    newCategory
            );

            // 카테고리 변경 시 카운트 조정
            if (oldCategory != null && !oldCategory.equals(newCategory)) {
                categoryService.decreaseCategoryCount(oldCategory);
            }
            if (newCategory != null && !newCategory.equals(oldCategory)) {
                categoryService.increaseCategoryCount(newCategory);
            }

            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 수정되었습니다.");
            return "redirect:/products/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 수정 중 오류가 발생했습니다.");
            return "redirect:/products/" + id + "/edit";
        }
    }

    //상품 삭제 처리
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                                HttpSession session,
                                @AuthenticationPrincipal OAuth2User oauth2User,
                                RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session, oauth2User);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/users/login";
        }

        try {
            Optional<Product> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "존재하지 않는 상품입니다.");
                return "redirect:/products";
            }

            Product product = productOpt.get();

            // 소유자 체크
            if (!product.getSeller().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "삭제 권한이 없습니다.");
                return "redirect:/products/" + id;
            }

            // 카테고리 정보 저장 (삭제 후 카운트 감소용)
            Category category = product.getCategory();

            // 상품 삭제
            productService.deleteProduct(id);

            // 카테고리 상품 수 감소
            if (category != null) {
                categoryService.decreaseCategoryCount(category);
            }

            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 삭제되었습니다.");
            return "redirect:/products";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 삭제 중 오류가 발생했습니다.");
            return "redirect:/products/" + id;
        }
    }

    /**
     * OAuth2 로그인과 기존 세션 로그인을 모두 지원하는 헬퍼 메서드
     */
    private User getCurrentUser(HttpSession session, OAuth2User oauth2User) {
        // 1. OAuth2 로그인 사용자 확인
        if (oauth2User != null) {
            Map<String, Object> attributes = oauth2User.getAttributes();
            Object userObj = attributes.get("user");
            if (userObj instanceof User) {
                return (User) userObj;
            }
        }

        // 2. 기존 세션 로그인 사용자 확인 (하위 호환성)
        Object sessionUser = session.getAttribute("user");
        if (sessionUser instanceof User) {
            return (User) sessionUser;
        }

        return null;
    }
}