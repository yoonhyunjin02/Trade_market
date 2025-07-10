package com.owl.trade_market.controller;

import com.owl.trade_market.dto.UserDto;
import com.owl.trade_market.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;


    //로그인 페이지 이동
    @GetMapping("/login")
    public String loginForm(@RequestHeader(value = "Referer", required = false) String referer,
                            HttpSession session) {

        // 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 상태 체크: 로그인상태라면 원래 페이지로 리다이렉트
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            // referer가 null이거나, 자기 자신(/login)에서 다시 들어온 경우는 메인으로
            if (referer == null || referer.contains("/login")) {
                return "redirect:/main";
            }

            return "redirect:" + referer;
        }

        // 로그인 전 머물던 URL을 세션에 저장
        // 로그인/회원가입 페이지에서 온 경우는 저장하지 않음
        if (referer != null && !referer.contains("/login") && !referer.contains("/register")) {
            session.setAttribute("previousUrl", referer);
        }
        return "pages/login";
    }

    // spring security 사용으로 잠시 주석처리
    //로그인
//    @PostMapping("/users/login")
//    public String login(@RequestParam String userId,
//                        @RequestParam String userPassword,
//                        HttpSession session,
//                        RedirectAttributes redirectAttributes) {
//        try {
//            Optional<User> user = userService.login(userId, userPassword);
//            if (user.isPresent()) {
//                session.setAttribute("user", user.get());
//                return "redirect:/main";
//            } else {
//                redirectAttributes.addFlashAttribute("error", "사용자명 또는 비밀번호가 올바르지 않습니다.");
//                return "redirect:/login";
//            }
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "로그인 중 오류가 발생했습니다.");
//            return "redirect:/login";
//        }
//    }

    //회원가입 페이지 이동
    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("userDto")) {
            model.addAttribute("userDto", new UserDto());
        }

        return "pages/register";
    }

    //회원가입
    @PostMapping("/users/register")
    public String register(@ModelAttribute UserDto userDto,
                           RedirectAttributes redirectAttributes) {


        try {
            if (!userDto.isPasswordMatching()) {
                redirectAttributes.addFlashAttribute("userDto", userDto);
                redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
                return "redirect:/register";
            }

            if (userDto.getUserId().length() < 3) {
                redirectAttributes.addFlashAttribute("userDto", userDto);
                redirectAttributes.addFlashAttribute("error", "사용자명은 3자 이상이어야 합니다.");
                return "redirect:/register";
            }

            if (userDto.getUserPassword().length() < 6) {
                redirectAttributes.addFlashAttribute("userDto", userDto);
                redirectAttributes.addFlashAttribute("error", "비밀번호는 6자 이상이어야 합니다.");
                return "redirect:/register";
            }

            userService.register(userDto);
            redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("userDto", userDto);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("userDto", userDto);
            redirectAttributes.addFlashAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "redirect:/register";
        }

    }

    // spring security 사용으로 잠시 주석처리
//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        session.invalidate();
//        return "redirect:/main";
//    }
}
