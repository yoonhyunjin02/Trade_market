package com.owl.trade_market.controller;

import com.owl.trade_market.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LocationController {

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey; // application.properties에 적어놓은 키를 담는 객체

    // 1) 폼 화면 표시 (currentAddress 더미값 세팅)
    @GetMapping("/location")
    public String showLocationForm(Model model) {
        model.addAttribute("currentAddress", "");  // 더미 주소
        model.addAttribute("googleMapsApiKey", googleMapsApiKey); // Google Map API 키 주입
        return "pages/location";
    }

    // 2) 사용자가 입력한 주소를 받아 같은 뷰로 렌더링
    @PostMapping("/api/location")
    public String submitLocation(
            @RequestParam("address") String address,
            Model model
    ) {
        model.addAttribute("currentAddress", address);
        return "pages/location";
    }

    // 3) 동네 인증 확정 처리
    @PostMapping("/api/location/confirm")
    public String confirmLocation(
            @RequestParam("address") String address,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");
        user.setUserLocation(address);
        session.setAttribute("user", user);
        // 또는 userService.updateLocation(user.getId(), address);
        return "redirect:/main";
    }



}
