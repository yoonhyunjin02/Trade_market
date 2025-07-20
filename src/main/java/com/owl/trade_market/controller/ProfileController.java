package com.owl.trade_market.controller;

import com.owl.trade_market.entity.User;
import com.owl.trade_market.entity.UserDetails;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.service.UserService;
import com.owl.trade_market.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;


    /**
     * 프로필 페이지
     * URL: /profile/edit
     */
    @GetMapping
    public String viewProfile(@RequestParam(required = false) String userIdString,
                              HttpSession session,
                              @AuthenticationPrincipal OAuth2User oauth2User,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        // 1. 현재 로그인한 사용자 확인
        User currentUser = getCurrentUser(session, oauth2User);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        // 2. 조회할 대상 사용자 결정
        User targetUser;
        boolean isOwnProfile;

        if (userIdString == null || userIdString.trim().isEmpty()) {
            targetUser = currentUser;
            isOwnProfile = true;
        } else {
            Optional<User> userOptional = userService.findByUserId(userIdString);
            if (userOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "존재하지 않는 사용자입니다.");
                return "redirect:/main";
            }
            targetUser = userOptional.get();
            isOwnProfile = currentUser.getId().equals(targetUser.getId());
        }

        // 3. UserDetails 조회 (없으면 자동 생성)
        UserDetails userDetails = userService.findUserDetailsByUser(targetUser)
                .orElse(userService.createUserDetails(targetUser));

        // 4. 해당 사용자의 상품 목록 조회
        List<Product> userProducts = productService.findBySeller(targetUser,
                Sort.by("createdAt").descending());

        // 5. 뷰에 데이터 전달
        model.addAttribute("user", targetUser);
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("userProducts", userProducts);
        model.addAttribute("isOwnProfile", isOwnProfile);

        return "pages/profile";
    }

    /**
     * 프로필 수정 페이지
     * URL: /profile/edit
     */

    /**
     * 프로필 수정 처리
     * URL: /profile/edit (POST)
     */





    private User getCurrentUser(HttpSession session, OAuth2User oauth2User) {
        // 1. OAuth2 로그인 사용자 확인 (Google 로그인)
        if (oauth2User != null) {
            Map<String, Object> attributes = oauth2User.getAttributes();
            Object userObj = attributes.get("user");
            if (userObj instanceof User) {
                return (User) userObj;
            }
        }

        // 2. 기존 세션 로그인 사용자 확인 (일반 로그인)
        Object sessionUser = session.getAttribute("user");
        if (sessionUser instanceof User) {
            return (User) sessionUser;
        }

        return null; // 로그인하지 않은 사용자
    }
}