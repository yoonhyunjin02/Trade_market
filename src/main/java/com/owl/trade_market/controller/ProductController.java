package com.owl.trade_market.controller;

import com.owl.trade_market.dto.ProductDto;
import com.owl.trade_market.entity.Category;

import com.owl.trade_market.entity.Image;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.service.CategoryService;
import com.owl.trade_market.service.ImageUploadService;
import com.owl.trade_market.service.ProductService;

import com.owl.trade_market.service.S3Service;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private S3Service s3Service;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    //상품 목록 초기 페이지 (trade.html)
    @GetMapping
    public String productList(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "16") int size,
                              @RequestParam(required = false) String sort,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) String location,
                              @RequestParam(required = false) Boolean availableOnly,
                              @RequestParam(required = false) Integer minPrice,
                              @RequestParam(required = false) Integer maxPrice,
                              Model model,
                              HttpSession session,
                              @AuthenticationPrincipal OAuth2User oauth2User) {

        User user = getCurrentUser(session, oauth2User);
        model.addAttribute("user", user);

        try {
            Sort sortOption;
            if (sort == null || sort.isEmpty() || "views".equals(sort)) {
                sortOption = Sort.by("viewCount").descending();
            } else if ("chats".equals(sort)) {
                sortOption = Sort.by("chatCount").descending();
            } else if ("latest".equals(sort)) {
                sortOption = Sort.by("createdAt").descending();
            } else if ("priceAsc".equals(sort)) {
                sortOption = Sort.by("price").ascending();
            } else if ("priceDesc".equals(sort)) {
                sortOption = Sort.by("price").descending();
            } else {
                sortOption = Sort.by("viewCount").descending();
            }

            Pageable pageable = PageRequest.of(page, size, sortOption);

            // 카테고리 조회
            Category selectedCategory = null;
            if (categoryId != null) {
                selectedCategory = categoryService.findById(categoryId).orElse(null);
            }

            // 가격 필터 기본값 보정
            Integer minBound = productService.findMinPrice();
            Integer maxBound = productService.findMaxPrice();

            if (minPrice == null || minPrice < minBound) {
                minPrice = minBound;
            }
            if (maxPrice == null || maxPrice > maxBound) {
                maxPrice = maxBound;
            }
            if (minPrice > maxPrice) { // 사용자가 거꾸로 넣은 경우 swap
                int tmp = minPrice;
                minPrice = maxPrice;
                maxPrice = tmp;
            }

            Page<Product> productPage = productService.filterProducts(
                    keyword,
                    selectedCategory,
                    minPrice,
                    maxPrice,
                    location,
                    availableOnly,
                    pageable
            );

            model.addAttribute("products", productPage.getContent());
            model.addAttribute("hasNext", productPage.hasNext());
            model.addAttribute("keyword", keyword);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("selectedCategory", selectedCategory);

            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);

            model.addAttribute("currentLocation", location);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("locations", productService.getAllDistinctLocations());

            model.addAttribute("currentSort", sort == null ? "views" : sort);
            model.addAttribute("availableOnly", availableOnly);

            // trade 페이지 필터에 맞게 제목 수정
            String pageTitle;
            
            if (sort == null || sort.equals("views")) {
                pageTitle = "중고거래 인기매물";
            } else if ("chats".equals(sort)) {
                pageTitle = "채팅 많은 순 매물";
            } else if ("latest".equals(sort)) {
                pageTitle = "최신 순 매물";
            } else if ("priceAsc".equals(sort)) {
                pageTitle = "가격 낮은 순 매물";
            } else if ("priceDesc".equals(sort)) {
                pageTitle = "가격 높은 순 매물";
            } else {
                pageTitle = "중고거래 인기매물";
            }
            
            List<String> conditions = new ArrayList<>();
            if (selectedCategory != null) conditions.add(selectedCategory.getName());
            if (location != null && !location.isBlank()) conditions.add(location);
            if (Boolean.TRUE.equals(availableOnly)) conditions.add("거래 가능");

            if (!conditions.isEmpty()) {
                pageTitle = String.join(" · ", conditions) + " " + pageTitle;
            }

            model.addAttribute("pageTitle", pageTitle);

        } catch (Exception e) {
            model.addAttribute("error", "상품 목록을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("hasNext", false);
        }

        return "pages/trade";
    }


    // 무한 스크롤
    @GetMapping("/scroll")
    public String scrollPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "16") int size,
            @RequestParam(defaultValue = "views") String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean availableOnly,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            Model model) {

        Sort sortOption;
        if (sort == null || sort.isEmpty() || "views".equals(sort)) {
            sortOption = Sort.by("viewCount").descending();
        } else if ("chats".equals(sort)) {
            sortOption = Sort.by("chatCount").descending();
        } else if ("latest".equals(sort)) {
            sortOption = Sort.by("createdAt").descending();
        } else if ("priceAsc".equals(sort)) {
            sortOption = Sort.by("price").ascending();
        } else if ("priceDesc".equals(sort)) {
            sortOption = Sort.by("price").descending();
        } else {
            sortOption = Sort.by("viewCount").descending();
        }

        Pageable pageable = PageRequest.of(page, size, sortOption);

        Category selectedCategory = null;
        if (categoryId != null) {
            selectedCategory = categoryService.findById(categoryId).orElse(null);
        }

        Integer minBound = productService.findMinPrice();
        Integer maxBound = productService.findMaxPrice();
        if (minPrice == null || minPrice < minBound) minPrice = minBound;
        if (maxPrice == null || maxPrice > maxBound) maxPrice = maxBound;

        Page<Product> productPage = productService.filterProducts(
                keyword,
                selectedCategory,
                minPrice,
                maxPrice,
                location,
                availableOnly,
                pageable
        );

        if (productPage.isEmpty()) {
            model.addAttribute("products", Collections.emptyList());
            return "fragments/product-card-list :: fragment";
        }

        model.addAttribute("products", productPage.getContent());
        return "fragments/product-card-list :: fragment";
    }


    // 검색 페이지 (search.html)
    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model,
                                 HttpSession session,
                                 @AuthenticationPrincipal OAuth2User oauth2User) {

        // 로그인 사용자 세팅
        User user = getCurrentUser(session, oauth2User);
        model.addAttribute("user", user);

        // 키워드 유효성 검사
        if (keyword == null || keyword.trim().isEmpty()) {
            model.addAttribute("error", "검색어를 입력해주세요.");
            model.addAttribute("products", java.util.Collections.emptyList());
            return "pages/search";
        }

        try {
            Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());

            Page<Product> searchPage = productService.searchProduct(keyword.trim(), pageable);

            model.addAttribute("page", searchPage);
            model.addAttribute("products", searchPage.getContent());
            model.addAttribute("keyword", keyword);

            String pageTitle;
            if (keyword != null && !keyword.trim().isEmpty()) {
                pageTitle = "'" + keyword.trim() + "' 에 대한 검색 결과 매물";
            } else {
                pageTitle = "검색 결과 매물";
            }
            model.addAttribute("pageTitle", pageTitle);

        } catch (Exception e) {
            model.addAttribute("error", "검색 중 오류가 발생했습니다.");
            model.addAttribute("products", java.util.Collections.emptyList());
        }

        return "pages/search";
    }

    //상품 등록 폼 페이지 (write.html)
    @GetMapping("/new")
    public String writeForm(Model model,
                            HttpSession session,
                            @AuthenticationPrincipal OAuth2User oauth2User,
                            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session, oauth2User);
        model.addAttribute("googleMapsApiKey",googleMapsApiKey);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        if (user.getUserLocation() == null || user.getUserLocation().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "상품 등록은 '내 동네 설정'이 필요합니다.");
            return "pages/location";
        }

        // FlashAttribute나 Session에 남은 이전 productDto 제거
        model.asMap().remove("productDto");
        session.removeAttribute("productDto");

        model.addAttribute("user", user);
        model.addAttribute("productDto", new ProductDto());

        // 완전히 비어있는 DTO 생성 (categoryName == null)
        ProductDto emptyDto = new ProductDto();  // price=0만 초기화, 나머지는 null
        emptyDto.setCategoryName(null);          // 명시적으로 null 보장
        model.addAttribute("productDto", emptyDto);

        // 카테고리 목록 조회
        model.addAttribute("categories", categoryService.findAll());

        // Google Map API 키 주입
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);


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
        // Google Map API 키 주입
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/users/login";
        }
        if (user.getUserLocation() == null || user.getUserLocation().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "상품 등록은 '내 동네 설정'이 필요합니다.");
            return "redirect:/pages/location";
        }

        // 유효성 실패 시에도 categories를 다시 세팅해야 함
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("productDto", productDto);
            model.addAttribute("categories", categoryService.findAll());
            return "pages/write";
        }

        try {
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

            // 선택된 카테고리명으로 Category 엔티티 찾거나 생성
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
                    productDto.getLocation(),
                    category
            );

            if (category != null) {
                categoryService.increaseCategoryCount(category);
            }

            // 이미지 업로드 처리 추가
            if (productDto.getImageFile() != null) {
                System.out.println("✅ 이미지 파일 넘어옴: " + productDto.getImageFile().getOriginalFilename());
                System.out.println("✅ 이미지 파일 크기: " + productDto.getImageFile().getSize());
            } else {
                System.out.println("❌ 이미지 파일이 null 입니다!");
            }

            // 이미지 업로드 처리 추가
            if (productDto.getImageFile() != null && !productDto.getImageFile().isEmpty()) {
                try {
                    imageUploadService.uploadProductImage(product.getId(), productDto.getImageFile());
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "이미지 업로드 중 오류가 발생했습니다.");
                    return "redirect:/products/new";
                }
            }

            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 등록되었습니다.");
            return "redirect:/products/" + product.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 등록 중 오류가 발생했습니다.");
            return "redirect:/products/new";
        }
    }

    // 상품상세 페이지 (trade-post.html)
    @GetMapping("/{id:[0-9]+}")
    public String productDetail(@PathVariable Long id,
                                Model model,
                                HttpSession session,
                                @AuthenticationPrincipal OAuth2User oauth2User,
                                RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session, oauth2User);
        model.addAttribute("user", user);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        try {
            Product product = productService.findByIdWithImages(id)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));

            // 조회수 증가
            productService.increaseViewCount(id);

            model.addAttribute("product", product);

            // 현재 사용자가 판매자인지 확인
            boolean isOwner = (user != null && user.getId().equals(product.getSeller().getId()));
            model.addAttribute("isOwner", isOwner);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/products";
        }

        return "pages/trade-post";
    }


    //상품 수정 폼 페이지
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Model model,
                           HttpSession session,
                           @AuthenticationPrincipal OAuth2User oauth2User,
                           RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session, oauth2User);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/users/login";
        }

        try {
            Product product = productService.findByIdWithImages(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

            // ✅ 소유자 체크
            if (!product.getSeller().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
                return "redirect:/products/" + id;
            }

            // ✅ Product → ProductDto 변환
            ProductDto productDto = new ProductDto();
            productDto.setTitle(product.getTitle());              // ✅ getProductTitle → getTitle
            productDto.setDescription(product.getDescription());  // ✅ getProductDescription → getDescription
            productDto.setPrice(product.getPrice());              // ✅ getProductPrice → getPrice
            productDto.setLocation(product.getLocation());        // ✅ getProductLocation → getLocation
            if (product.getCategory() != null) {
                productDto.setCategoryName(product.getCategory().getName());
            }

            // ✅ 기존 이미지가 있으면 첫 번째 URL 넘기기
            String existingImageUrl = null;
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                existingImageUrl = product.getImages().get(0).getImage();
            }

            // ✅ 모델에 담기
            model.addAttribute("user", user);
            model.addAttribute("productDto", productDto);
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("existingImageUrl", existingImageUrl); // ✅ 추가

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/products/" + id;
        }

        return "pages/write";
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
            model.addAttribute("categories", categoryService.findAll());
            return "pages/write";
        }

        try {
            Product product = productService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

            // ✅ 소유자 체크
            if (!product.getSeller().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
                return "redirect:/products/" + id;
            }

            // ✅ 기존 카테고리 저장
            Category oldCategory = product.getCategory();

            // ✅ 새 카테고리 생성/찾기
            Category newCategory = null;
            if (productDto.getCategoryName() != null && !productDto.getCategoryName().trim().isEmpty()) {
                newCategory = categoryService.findOrCreateCategory(productDto.getCategoryName().trim());
            }

            // ✅ 상품 정보 업데이트 (DB 저장 포함)
            productService.updateProduct(
                    id,
                    productDto.getTitle().trim(),
                    productDto.getDescription().trim(),
                    productDto.getPrice(),
                    productDto.getLocation(),
                    newCategory
            );

            // ✅ 이미지 새 업로드 시 교체
            if (productDto.getImageFile() != null && !productDto.getImageFile().isEmpty()) {
                System.out.println("🔄 새 이미지 업로드됨 → 기존 이미지 교체 진행");
                imageUploadService.replaceProductImage(product.getId(), productDto.getImageFile());
            }

            // ✅ 카테고리 카운트 변경
            if (oldCategory != null && !oldCategory.equals(newCategory)) {
                categoryService.decreaseCategoryCount(oldCategory);
            }
            if (newCategory != null && !newCategory.equals(oldCategory)) {
                categoryService.increaseCategoryCount(newCategory);
            }

            // ✅ 수정 후 상세 페이지로 이동
            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 수정되었습니다.");
            return "redirect:/products/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 수정 중 오류가 발생했습니다: " + e.getMessage());
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
            // ✅ 상품 + 이미지까지 로딩
            Product product = productService.findByIdWithImages(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

            // ✅ 소유자 체크
            if (!product.getSeller().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "삭제 권한이 없습니다.");
                return "redirect:/products/" + id;
            }

            // ✅ 기존 이미지가 있다면 S3에서 삭제
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                System.out.println("🗑 기존 이미지 개수: " + product.getImages().size());
                for (Image img : product.getImages()) {
                    // URL → Key 변환
                    String key = s3Service.extractKeyFromUrl(img.getImage());
                    System.out.println("🗑 S3 이미지 삭제 key=" + key);
                    s3Service.deleteFile(key);
                }
            } else {
                System.out.println("➡ 삭제할 이미지 없음");
            }

            // ✅ 카테고리 정보 저장 (삭제 후 카운트 감소용)
            Category category = product.getCategory();

            // ✅ 상품 삭제 (Image 엔티티는 cascade로 자동 삭제)
            productService.deleteProduct(id);

            // ✅ 카테고리 상품 수 감소
            if (category != null) {
                categoryService.decreaseCategoryCount(category);
            }

            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 삭제되었습니다.");
            return "redirect:/products";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "상품 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/products/" + id;
        }
    }

    // OAuth2 로그인과 기존 세션 로그인을 모두 지원하는 헬퍼 메서드
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