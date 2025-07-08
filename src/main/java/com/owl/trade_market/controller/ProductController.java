package com.owl.trade_market.controller;

import com.owl.trade_market.dto.ProductDto;
import com.owl.trade_market.entity.Category;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 상품 목록 페이지 (trade.html)
     * 모든 상품을 최신순으로 표시 - 페이징 없이 전체 로드
     */
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

            if (keyword != null && !keyword.trim().isEmpty()) {
                // 검색 시에도 전체 결과 반환
                Page<Product> searchPage = productService.searchProduct(
                        keyword.trim(), null,
                        Pageable.unpaged(Sort.by("createdAt").descending())
                );
                allProducts = searchPage.getContent();
            } else {
                // 전체 상품 조회
                allProducts = productService.findAll(Sort.by("createdAt").descending());
            }

            model.addAttribute("products", allProducts);
            model.addAttribute("keyword", keyword);
            model.addAttribute("categoryId", categoryId);

        } catch (Exception e) {
            model.addAttribute("error", "상품 목록을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("products", java.util.Collections.emptyList());
        }

        return "pages/trade";
    }

    /**
     * 상품 등록 폼 페이지 (write.html)
     */
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
        // TODO: 카테고리 목록 추가 (CategoryService 구현 후)
        // List<Category> categories = categoryService.findAll();
        // model.addAttribute("categories", categories);

        return "pages/write";
    }

    /**
     * 상품 등록 처리
     */
    @PostMapping("/new")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto,
                                @RequestParam(required = false) Long categoryId,
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
            // TODO: 카테고리 목록 다시 설정
            return "pages/write";
        }

        try {
            // 추가 유효성 검사
            if (productDto.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "상품 제목을 입력해주세요.");
                return "redirect:/api/products/new";
            }

            if (productDto.getDescription().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "상품 설명을 입력해주세요.");
                return "redirect:/api/products/new";
            }

            if (productDto.getPrice() <= 0) {
                redirectAttributes.addFlashAttribute("error", "올바른 가격을 입력해주세요.");
                return "redirect:/api/products/new";
            }

            // TODO: Category 조회 로직 (임시로 null 처리)
            Category category = null;
            // if (categoryId != null) {
            //     Optional<Category> categoryOpt = categoryService.findById(categoryId);
            //     category = categoryOpt.orElse(null);
            // }

            // 상품 생성
            Product product = productService.createProduct(
                    productDto.getTitle().trim(),
                    user,
                    productDto.getDescription().trim(),
                    productDto.getPrice(),
                    user.getUserLocation(), // 사용자 위치 사용
                    category
            );

            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 등록되었습니다.");
            return "redirect:/api/products/" + product.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 등록 중 오류가 발생했습니다.");
            return "redirect:/api/products/new";
        }
    }

    /**
     * 상품 상세 페이지 (trade_post.html)
     */
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
                return "redirect:/api/products";
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
            return "redirect:/api/products";
        }

        return "pages/trade_post";
    }

    /**
     * 상품 수정 폼 페이지
     */
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
                return "redirect:/api/products";
            }

            Product product = productOpt.get();

            // 소유자 체크
            if (!product.getSeller().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
                return "redirect:/api/products/" + id;
            }

            // ProductDto로 변환
            ProductDto productDto = new ProductDto(
                    product.getTitle(),
                    product.getDescription(),
                    product.getPrice()
            );

            model.addAttribute("user", user);
            model.addAttribute("productDto", productDto);
            model.addAttribute("productId", id);
            // TODO: 카테고리 목록 추가

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/api/products/" + id;
        }

        return "pages/write"; // 같은 폼 재사용
    }

    /**
     * 상품 수정 처리
     */
    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute ProductDto productDto,
                                @RequestParam(required = false) Long categoryId,
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
            return "pages/write";
        }

        try {
            // 추가 유효성 검사
            if (productDto.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "상품 제목을 입력해주세요.");
                return "redirect:/api/products/" + id + "/edit";
            }

            if (productDto.getDescription().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "상품 설명을 입력해주세요.");
                return "redirect:/api/products/" + id + "/edit";
            }

            Optional<Product> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "존재하지 않는 상품입니다.");
                return "redirect:/api/products";
            }

            Product product = productOpt.get();

            // 소유자 체크
            if (!product.getSeller().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
                return "redirect:/api/products/" + id;
            }

            // TODO: Category 조회 로직
            Category category = null;
            // if (categoryId != null) {
            //     Optional<Category> categoryOpt = categoryService.findById(categoryId);
            //     category = categoryOpt.orElse(null);
            // }

            // 상품 정보 업데이트
            Product updatedProduct = productService.updateProduct(
                    id,
                    productDto.getTitle().trim(),
                    productDto.getDescription().trim(),
                    productDto.getPrice(),
                    user.getUserLocation(),
                    category
            );

            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 수정되었습니다.");
            return "redirect:/api/products/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 수정 중 오류가 발생했습니다.");
            return "redirect:/api/products/" + id + "/edit";
        }
    }

    /**
     * 상품 삭제 처리
     */
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
                return "redirect:/api/products";
            }

            Product product = productOpt.get();

            // 소유자 체크
            if (!product.getSeller().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "삭제 권한이 없습니다.");
                return "redirect:/api/products/" + id;
            }

            // 상품 삭제
            productService.deleteProduct(id);

            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 삭제되었습니다.");
            return "redirect:/api/products";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 삭제 중 오류가 발생했습니다.");
            return "redirect:/api/products/" + id;
        }
    }

    /**
     * 검색 페이지 (search.html)
     */
    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword,
                                 @RequestParam(required = false) Long categoryId,
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

            // TODO: Category 조회 로직
            Category category = null;
            // if (categoryId != null) {
            //     Optional<Category> categoryOpt = categoryService.findById(categoryId);
            //     category = categoryOpt.orElse(null);
            // }

            // 검색 결과 전체 반환
            Page<Product> searchPage = productService.searchProduct(
                    keyword.trim(), category,
                    Pageable.unpaged(Sort.by("createdAt").descending())
            );
            List<Product> searchResults = searchPage.getContent();

            model.addAttribute("products", searchResults);
            model.addAttribute("keyword", keyword);
            model.addAttribute("categoryId", categoryId);

        } catch (Exception e) {
            model.addAttribute("error", "검색 중 오류가 발생했습니다.");
            model.addAttribute("products", java.util.Collections.emptyList());
        }

        return "pages/search";
    }

    /**
     * 카테고리별 상품 조회
     */
    @GetMapping("/category/{categoryId}")
    public String productsByCategory(@PathVariable Long categoryId,
                                     Model model,
                                     HttpSession session,
                                     @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getCurrentUser(session, oauth2User);
        model.addAttribute("user", user);

        try {
            // TODO: Category 조회 및 해당 카테고리 상품 조회 로직
            // Optional<Category> categoryOpt = categoryService.findById(categoryId);
            // if (categoryOpt.isEmpty()) {
            //     model.addAttribute("error", "존재하지 않는 카테고리입니다.");
            //     model.addAttribute("products", java.util.Collections.emptyList());
            //     return "pages/trade";
            // }

            // Category category = categoryOpt.get();
            // List<Product> categoryProducts = productService.findByCategory(category, Sort.by("createdAt").descending());

            // 임시로 전체 조회
            List<Product> allProducts = productService.findAll(Sort.by("createdAt").descending());

            model.addAttribute("products", allProducts);
            model.addAttribute("categoryId", categoryId);

        } catch (Exception e) {
            model.addAttribute("error", "카테고리 상품을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("products", java.util.Collections.emptyList());
        }

        return "pages/trade";
    }

    /**
     * 별칭 URL 처리 (/trade -> /api/products)
     */
    @GetMapping("/trade")
    public String tradeAlias() {
        return "redirect:/api/products";
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