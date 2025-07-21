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

    /** 버튼 질문과 완전히 일치하는 FAQ 응답 반환 */
    public static String findFaqByQuestion(String exactQuestion) {
        return faqList.stream()
                .filter(entry -> entry.getQuestion().equals(exactQuestion))
                .map(FaqEntry::getAnswer)
                .findFirst()
                .orElse(null);
    }

    public static class FaqEntry {
        private String question;
        private String answer;

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }
}