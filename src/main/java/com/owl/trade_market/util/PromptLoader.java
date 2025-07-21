package com.owl.trade_market.util;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PromptLoader {

    private static final String ANSWER_RESPONSE_RULES= """
            📌 **답변 시 규칙**
            - 반드시 친절하고 간결하게 설명
            - 필요한 경우 **구체적 예시 포함**
            - 정책에 없는 내용은 **“정확한 안내가 어려우니 고객센터에 문의해주세요.”**라고 안내
            - 반드시 **한국어**로 답변
            """;

    public static String loadPrompt(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + fileName);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("프롬프트 로딩 실패: " + fileName, e);
        }
    }

    public static String buildPrompt(String fileName, String userInput) {
        String template = loadPrompt(fileName);
        template = template.replace("{{response_rules}}", ANSWER_RESPONSE_RULES);
        template = template.replace("{{user_input}}", userInput);
        return template;
    }
}