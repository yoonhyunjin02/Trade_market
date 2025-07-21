package com.owl.trade_market.util;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PromptLoader {

    public static String loadPrompt(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + fileName);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("프롬프트 로딩 실패: " + fileName, e);
        }
    }

    /**
     * {{user_input}} 치환해서 완성된 프롬프트 반환
     */
    public static String buildPrompt(String fileName, String userInput) {
        String template = loadPrompt(fileName);
        return template.replace("{{user_input}}", userInput);
    }
}
