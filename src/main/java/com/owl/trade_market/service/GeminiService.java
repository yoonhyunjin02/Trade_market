package com.owl.trade_market.service;

import com.owl.trade_market.dto.GeminiMessageDto;
import java.util.List;

/**
 * Gemini AI 호출 서비스 인터페이스
 */
public interface GeminiService {

    /**
     * 대화 히스토리 + 현재 질문을 기반으로 AI 응답 생성
     * @param history 기존 대화 히스토리 (사용자 & 챗봇)
     * @param question 이번 질문
     * @return Gemini AI 응답 텍스트
     */
    String ask(List<GeminiMessageDto> history, String question);

    // ✅ 새로운 메서드 추가
    String askWithPrompt(String promptTemplate, String userQuestion);
}
