package com.owl.trade_market.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class FaqLoader {

    private static List<FaqEntry> faqList;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            faqList = mapper.readValue(
                    new ClassPathResource("faq/faq.json").getInputStream(),
                    new TypeReference<>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("FAQ JSON 로딩 실패", e);
        }
    }

    public static String findAnswer(String question) {
        for (FaqEntry entry : faqList) {
            for (String keyword : entry.getKeywords()) {
                if (question.contains(keyword)) {
                    return entry.getAnswer();
                }
            }
        }
        return null; // 매칭되는 FAQ 없으면 null
    }

    public static class FaqEntry {
        private List<String> keywords;
        private String answer;

        public List<String> getKeywords() { return keywords; }
        public void setKeywords(List<String> keywords) { this.keywords = keywords; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }
}
