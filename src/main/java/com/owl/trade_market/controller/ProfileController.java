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
     * URL: /profile?userIdString=...
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

        // 3. UserDetails 조회 (없으면 자동 생성) - 개선된 로직
        UserDetails userDetails = getUserDetailsWithFallback(targetUser);

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
     * UserDetails를 가져오고, 없으면 생성하는 간단한 메서드
     */
    private UserDetails getUserDetailsWithFallback(User user) {
        try {
            // 기존 UserDetails 조회
            Optional<UserDetails> existingDetails = userService.findUserDetailsByUserId(user.getId());
            if (existingDetails.isPresent()) {
                return existingDetails.get();
            }

            // UserDetails가 없으면 생성
            System.out.println("UserDetails 없음, 생성 중: User ID " + user.getId());
            return userService.createUserDetails(user);

        } catch (Exception e) {
            System.err.println("UserDetails 처리 중 오류: " + e.getMessage());
            // 예외 발생 시 기본 UserDetails 반환 (메모리상에서만)
            return new UserDetails(user);
        }
    }


     //현재 로그인한 사용자를 가져오기
    private User getCurrentUser(HttpSession session, OAuth2User oauth2User) {
        // 1. OAuth2 로그인 사용자 확인 (Google 로그인)
        if (oauth2User != null) {
            Map<String, Object> attributes = oauth2User.getAttributes();
            Object userObj = attributes.get("user");
            if (userObj instanceof User user) {
                System.out.println("OAuth2 사용자 확인: " + user.getUserEmail());
                return user;
            }

            // attributes에서 user 객체를 찾을 수 없는 경우, 이메일로 조회 시도
            String email = oauth2User.getAttribute("email");
            if (email != null) {
                System.out.println("OAuth2 attributes에서 user 없음, 이메일로 조회: " + email);
                Optional<User> userByEmail = userService.findByUserId(email.substring(0, email.indexOf('@')));
                if (userByEmail.isPresent()) {
                    return userByEmail.get();
                }
            }
        }

        // 2. 기존 세션 로그인 사용자 확인 (일반 로그인)
        Object sessionUser = session.getAttribute("user");
        if (sessionUser instanceof User user) {
            System.out.println("세션 사용자 확인: " + user.getUserId());
            return user;
        }

        System.out.println("로그인한 사용자를 찾을 수 없음");
        return null; // 로그인하지 않은 사용자
    }
}