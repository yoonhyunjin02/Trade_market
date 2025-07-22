package com.owl.trade_market.controller;

import com.owl.trade_market.entity.User;
import com.owl.trade_market.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/location")
public class LocationController {

    private final UserService userService;

    public LocationController(UserService userService) {
        this.userService = userService;
    }

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    // 1) 폼 화면 표시 (currentAddress 더미값 세팅)
    @GetMapping
    public String showLocationForm(Model model) {
        model.addAttribute("currentAddress", "");
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "pages/location";
    }

    // 2) 사용자가 입력한 주소를 받아 같은 뷰로 렌더링
    @PostMapping
    public String submitLocation(
            @RequestParam("address") String address,
            Model model
    ) {
        model.addAttribute("currentAddress", address);
        return "pages/location";
    }

    // 3) 동네 인증 확정 처리 - 세션 업데이트 추가
    @PostMapping("/confirm")
    public String confirmLocation(
            @RequestParam("address") String address,
            Authentication authentication,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // principal 타입 검사
            Object principal = authentication.getPrincipal();
            String key; // 식별자: 로컬=userId, 소셜=email
            User updatedUser = null;

            if (principal instanceof UserDetails) {
                // 폼 로그인 사용자
                key = ((UserDetails) principal).getUsername();
                userService.updateLocation(key, address);

                // 업데이트된 사용자 정보 다시 조회
                Optional<User> userOptional = userService.findByUserId(key);
                if (userOptional.isPresent()) {
                    updatedUser = userOptional.get();
                    // 세션 업데이트
                    session.setAttribute("user", updatedUser);
                    System.out.println("일반 로그인 사용자 세션 업데이트 완료: " + key);
                }

            } else if (principal instanceof OAuth2User) {
                // OAuth2 로그인 사용자
                OAuth2User oauth2 = (OAuth2User) principal;
                key = oauth2.getAttribute("email");
                userService.updateLocationByEmail(key, address);

                // OAuth2 사용자의 경우 attributes에서 user 객체 업데이트
                Object userObj = oauth2.getAttribute("user");
                if (userObj instanceof User) {
                    updatedUser = (User) userObj;
                    updatedUser.setUserLocation(address); // 메모리상에서도 업데이트
                    System.out.println("OAuth2 사용자 정보 업데이트 완료: " + key);
                }

            } else {
                throw new IllegalStateException("알 수 없는 로그인 유형: " + principal.getClass());
            }

            redirectAttributes.addFlashAttribute("success", "동네 인증이 완료되었습니다!");
            return "redirect:/main";

        } catch (Exception e) {
            System.err.println("동네 인증 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "동네 인증 중 오류가 발생했습니다.");
            return "redirect:/location";
        }
    }
}