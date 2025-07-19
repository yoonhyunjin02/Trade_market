package com.owl.trade_market.service.impl;

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

    @Value("${GOOGLE_API_KEY}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";

    @Override
    public String ask(List<GeminiMessageDto> history, String question) {
        // ✅ JSON 요청 바디 구성
        String prompt = question; // 필요시 history 합쳐서 prompt 구성

        String requestBody = """
        {
          "contents": [{
            "parts": [{"text": "%s"}]
          }]
        }
        """.formatted(prompt);

        RestTemplate rest = new RestTemplate();

        // ✅ POST 요청
        ResponseEntity<Map> response = rest.postForEntity(
                GEMINI_URL + apiKey,
                new HttpEntity<>(requestBody, createJsonHeader()),
                Map.class
        );

        // ✅ 응답 파싱
        Map body = response.getBody();
        if (body == null) return "⚠️ Gemini 응답 없음";

        List<Map<String,Object>> candidates = (List<Map<String,Object>>) body.get("candidates");
        if (candidates == null || candidates.isEmpty()) return "⚠️ 답변 없음";

        Map<String,Object> content = (Map<String,Object>) candidates.get(0).get("content");
        List<Map<String,String>> parts = (List<Map<String,String>>) content.get("parts");

        return parts.get(0).get("text");
    }

    private HttpHeaders createJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}