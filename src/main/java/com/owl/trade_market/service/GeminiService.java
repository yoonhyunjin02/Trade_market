package com.owl.trade_market.service;

import com.owl.trade_market.dto.GeminiMessageDto;
import java.util.List;

/**
 * Gemini AI 호출 서비스 인터페이스
 */
public interface GeminiService {
    String askWithPrompt(String promptTemplate, String userQuestion);
}
