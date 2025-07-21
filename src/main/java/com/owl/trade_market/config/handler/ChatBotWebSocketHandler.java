package com.owl.trade_market.config.handler;

import com.owl.trade_market.dto.GeminiMessageDto;
import com.owl.trade_market.service.GeminiService;
import com.owl.trade_market.util.FaqLoader;
import com.owl.trade_market.util.PromptLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatBotWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatBotWebSocketHandler.class);

    // 카테고리 → 프롬프트 매핑
    private static final Map<String, String> CATEGORY_PROMPT_MAP = Map.of(
            "거래 문제", "transaction_issue.txt",
            "운영정책", "operation_policy.txt",
            "동네설정/거래범위", "location_scope.txt",
            "물건 구매하기", "buy_item.txt",
            "내 물건 팔기", "sell_item.txt",
            "알림", "notification.txt",
            "거래 매너", "manner.txt",
            "거래 금지 물품", "forbidden_items.txt"
    );

    private final GeminiService geminiService;
    private final Map<String, List<GeminiMessageDto>> userHistoryMap = new ConcurrentHashMap<>();

    public ChatBotWebSocketHandler(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("챗봇 WebSocket 연결됨: {}", session.getId());
        session.sendMessage(new TextMessage("챗봇 연결 완료! 질문을 입력해주세요."));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String userQuestion = message.getPayload();
        String userId = (String) session.getAttributes().get("userId");
        if (userId == null) userId = "guest";

        log.info("[챗봇 질문] userId={} → {}", userId, userQuestion);

        try {
            // 대화 초기화
            if ("__RESET_CHAT__".equals(userQuestion)) {
                userHistoryMap.remove(userId);
                session.sendMessage(new TextMessage("대화가 초기화되었습니다. 새로운 질문을 시작해주세요!"));
                return;
            }

            // FAQ 버튼 질문 매칭 (faq.json 기반)
            String faqAnswer = FaqLoader.findFaqByQuestion(userQuestion);
            if (faqAnswer != null) {
                log.info("FAQ 버튼 매칭됨 → 즉시 응답 반환");
                session.sendMessage(new TextMessage(faqAnswer));
                return;
            }

            // 버튼 FAQ가 아닌 일반 질문이면 → 카테고리 분류 후 Gemini API
            String category;
            try {
                category = classifyCategory(userQuestion).trim();
                log.info("분류된 카테고리: {}", category);

                if ("모름".equals(category)) {
                    log.info("카테고리 분류 결과: 모름 → 통합 프롬프트 fallback");
                    String unifiedPrompt = PromptLoader.buildPrompt("unified.txt", userQuestion);
                    String unifiedAnswer = geminiService.askWithPrompt(unifiedPrompt, userQuestion);
                    if (unifiedAnswer == null || unifiedAnswer.isBlank()) {
                        unifiedAnswer = "정확한 안내가 어렵습니다. 고객센터에 문의해주세요.";
                    }
                    session.sendMessage(new TextMessage(unifiedAnswer));
                    return;
                }

            } catch (Exception ce) {
                log.warn("카테고리 분류 실패 → 통합 프롬프트 fallback", ce);
                category = "모름";
            }

            // 히스토리 관리 및 Gemini 응답
            List<GeminiMessageDto> history =
                    userHistoryMap.computeIfAbsent(userId, k -> new ArrayList<>());
            history.add(new GeminiMessageDto(1L, userQuestion));

            String botAnswer;
            try {
                String prompt;

                if (CATEGORY_PROMPT_MAP.containsKey(category)) {
                    String promptFile = CATEGORY_PROMPT_MAP.get(category);
                    log.info("로드할 프롬프트 파일: {}", promptFile);

                    prompt = PromptLoader.buildPrompt(promptFile, userQuestion);
                } else {
                    log.info("카테고리 매칭 실패 → 통합 프롬프트 fallback");
                    prompt = PromptLoader.buildPrompt("unified.txt", userQuestion);
                }

                botAnswer = geminiService.askWithPrompt(prompt, userQuestion);

                if (botAnswer == null || botAnswer.isBlank()) {
                    botAnswer = "AI 응답이 비어있습니다. 잠시 후 다시 시도해주세요.";
                } else if (botAnswer.length() < 20) {
                    botAnswer += "\n\n더 자세한 내용이 필요하시면 다시 물어봐주세요!";
                }

            } catch (Exception e) {
                log.error("Gemini API 호출 실패", e);
                botAnswer = "AI 서버 호출에 실패했습니다. 잠시 후 다시 시도해주세요.";
            }

            history.add(new GeminiMessageDto(0L, botAnswer));
            session.sendMessage(new TextMessage(botAnswer));

        } catch (Exception fatal) {
            log.error("handleTextMessage 치명적 예외 발생", fatal);
            safeSend(session, "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    private void safeSend(WebSocketSession session, String msg) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(msg));
            }
        } catch (Exception e) {
            log.error("safeSend 실패", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("챗봇 WebSocket 연결 종료: {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession s, Throwable ex) throws Exception {
        log.error("Transport error", ex);
        if (s.isOpen()) {
            s.sendMessage(new TextMessage("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
        }
    }

    private String classifyCategory(String userQuestion) {
        String prompt = PromptLoader.buildPrompt("category_classifier.txt", userQuestion);
        return geminiService.askWithPrompt(prompt, userQuestion).trim();
    }
}