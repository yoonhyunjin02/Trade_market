package com.owl.trade_market.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*
     * 정적 리소스 핸들러 등록
     *  - /css/**  → classpath:/static/css/
     *  - /js/**   → classpath:/static/js/
     *  - /images/** → classpath:/static/images/
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry
                .addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}
