package com.owl.trade_market.controller;

import com.owl.trade_market.entity.Image;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public String home() {
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String main(Model model) {
        List<Product> all = productService.findAll(Sort.by(Sort.Direction.DESC, "viewCount"));
        List<Product> top8 = all.size() > 8 ? all.subList(0, 8) : all;

        if (top8.isEmpty()) {
            model.addAttribute("defaultProducts", getDefaultProducts());
        } else {
            model.addAttribute("products", top8);
        }

        return "pages/main";
    }

    // 기본 데이터 샘플
    private List<Product> getDefaultProducts() {
        List<Product> defaultProducts = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {
            Product sample = new Product();
            sample.setId((long) i);
            sample.setTitle("예시 상품 " + i);
            sample.setPrice(10000 + i * 1000); // 예시로 가격 다르게
            sample.setLocation("서울시 강남구");
            sample.setViewCount(0);
            sample.setChatCount(0);

            Image image = new Image();
            image.setImage("/images/mascot.png");
            image.setProduct(sample);

            sample.getImages().add(image);

            defaultProducts.add(sample);
        }

        return defaultProducts;
    }

}
