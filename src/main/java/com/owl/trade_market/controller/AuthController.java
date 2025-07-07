package com.owl.trade_market.controller;

import com.owl.trade_market.dto.UserDto;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;


    //로그인 페이지 이동
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    //로그인
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            Optional<User> user = userService.login(username, password);
            if (user.isPresent()) {
                session.setAttribute("user", user.get());
                return "redirect:/main";
            } else {
                redirectAttributes.addFlashAttribute("error", "사용자명 또는 비밀번호가 올바르지 않습니다.");
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "로그인 중 오류가 발생했습니다.");
            return "redirect:/login";
        }

    }

    //회원가입 페이지 이동
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    //회원가입
    @PostMapping("/register")
    public String register(@RequestParam String userId,
                           @RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           RedirectAttributes redirectAttributes) {


        try {
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
                return "redirect:/register";
            }

            if (userId.length() < 3) {
                redirectAttributes.addFlashAttribute("error", "사용자명은 3자 이상이어야 합니다.");
                return "redirect:/register";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "비밀번호는 6자 이상이어야 합니다.");
                return "redirect:/register";
            }

            userService.register(new UserDto(userId, username, password));
            redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "redirect:/register";
        }

    }
}
