package com.owl.trade_market.controller;

import com.owl.trade_market.entity.Product;
import com.owl.trade_market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
        List<Product> products = productService.findAll(Sort.by(Sort.Direction.DESC, "viewCount"))
                .stream()
                .limit(8)
                .toList();

        model.addAttribute("products", products);
        return "pages/main";
    }
}

