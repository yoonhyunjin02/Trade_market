package com.owl.trade_market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LocationController {

    // 1) 폼 화면 표시 (currentAddress 더미값 세팅)
    @GetMapping("/location")
    public String showLocationForm(Model model) {
        model.addAttribute("currentAddress", "서울 강서구 화곡동");  // 더미 주소
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
            @RequestParam("address") String address
    ) {
        // address를 DB나 세션에 실제 저장하는 로직 추가
        // address를 받아서 사용자가 입력한 currentAddress와 비교
        // 인증이 완료되면 main 페이지로 돌아감 + address를 location에 저장
        return "pages/main";
    }
}
