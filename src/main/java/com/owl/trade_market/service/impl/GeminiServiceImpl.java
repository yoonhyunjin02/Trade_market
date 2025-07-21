package com.owl.trade_market.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.trade_market.dto.GeminiMessageDto;
import com.owl.trade_market.service.GeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiServiceImpl implements GeminiService {
    private static final Logger log = LoggerFactory.getLogger(GeminiServiceImpl.class);

    @Value("${GOOGLE_API_KEY}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String askWithPrompt(String promptTemplate, String userQuestion) {
        String fullPrompt = promptTemplate.replace("{{user_input}}", userQuestion);

        try {
            // JSON Map 생성
            Map<String, Object> requestMap = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", fullPrompt)
                            ))
                    )
            );

            // 직렬화
            String requestBody = objectMapper.writeValueAsString(requestMap);

            RestTemplate rest = new RestTemplate();

            ResponseEntity<Map> response = rest.postForEntity(
                    GEMINI_URL + apiKey,
                    new HttpEntity<>(requestBody, createJsonHeader()),
                    Map.class
            );

            Map body = response.getBody();
            if (body == null) return "Gemini 응답 없음";

            List<Map<String,Object>> candidates = (List<Map<String,Object>>) body.get("candidates");
            if (candidates == null || candidates.isEmpty()) return "답변 없음";

            Map<String,Object> content = (Map<String,Object>) candidates.get(0).get("content");
            List<Map<String,String>> parts = (List<Map<String,String>>) content.get("parts");

            return parts.get(0).get("text");
        } catch (Exception e) {
            log.error("Gemini API JSON 직렬화/호출 실패", e);
            return "AI 서버 요청 중 오류가 발생했습니다.";
        }
    }


    private String safeCallGeminiAPI(String prompt) {
        try {
            return callGeminiAPI(prompt);
        } catch (Exception e) {
            // 모든 예외를 잡아서 안전한 응답 반환
            System.err.println("Gemini API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            return "AI 서버 호출 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    // 실제 Gemini API 호출 (예외 발생 가능)
    private String callGeminiAPI(String finalPrompt) throws Exception {
        // JSON Map 생성 (Jackson이 알아서 안전하게 직렬화)
        Map<String, Object> requestMap = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", finalPrompt)
                        ))
                )
        );

        // Jackson 직렬화
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RestTemplate rest = new RestTemplate();

        ResponseEntity<Map> response = rest.postForEntity(
                GEMINI_URL + apiKey,
                new HttpEntity<>(requestBody, createJsonHeader()),
                Map.class
        );

        Map body = response.getBody();
        if (body == null) return "Gemini 응답 없음";

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
        if (candidates == null || candidates.isEmpty()) return "후보 답변 없음";

        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
        if (parts == null || parts.isEmpty()) return "응답 파싱 실패";

        return parts.get(0).get("text");
    }

    private HttpHeaders createJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

