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

    // 카테고리 → 프롬프트 파일 매핑
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

    // userId → 히스토리
    private final Map<String, List<GeminiMessageDto>> userHistoryMap = new ConcurrentHashMap<>();

    public ChatBotWebSocketHandler(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("챗봇 WebSocket 연결됨: {}", session.getId()); // 유저 아이디 확인
        session.sendMessage(new TextMessage("챗봇 연결 완료! 질문을 입력해주세요.")); // 연결 안내 메시지
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String userQuestion = message.getPayload();
        String userId = (String) session.getAttributes().get("userId");
        if (userId == null) userId = "guest";

        log.info("[챗봇 질문] userId={} → {}", userId, userQuestion); // 유저 아이디와 질문 내역 로그

        try { // 대화 저장하지 않고 초기화
            // 초기화 요청 처리
            if ("__RESET_CHAT__".equals(userQuestion)) {
                userHistoryMap.remove(userId);
                session.sendMessage(new TextMessage("대화가 초기화되었습니다. 새로운 질문을 시작해주세요!"));
                return;
            }

            // FAQ 매칭 먼저 시도
            // 재미나이 넘기기 전에 FAQ에 있는지 확인
            String faqAnswer = getLocalFaqAnswer(userQuestion);
            if (faqAnswer != null) {
                log.info("FAQ 매칭됨 → 즉시 답변 반환");
                session.sendMessage(new TextMessage(faqAnswer));
                return;
            }

            // 카테고리 분류
            String category = "기타";
            try {
                category = classifyCategory(userQuestion);
                log.info("분류된 카테고리: {}", category); // 분류된 카테고리 확인
            } catch (Exception ce) {
                log.warn("카테고리 분류 실패 → 기본 카테고리(기타) 사용", ce); // 실패시 일반 재미나이 호출
            }

            // 히스토리 관리
            List<GeminiMessageDto> history =
                    userHistoryMap.computeIfAbsent(userId, k -> new ArrayList<>());
            history.add(new GeminiMessageDto(1L, userQuestion));

            // 답변 생성
            String botAnswer;
            try {
                if (CATEGORY_PROMPT_MAP.containsKey(category)) {
                    String promptFile = CATEGORY_PROMPT_MAP.get(category);
                    log.info("로드할 프롬프트 파일: {}", promptFile);

                    String prompt = PromptLoader.buildPrompt(promptFile, userQuestion);
                    log.debug("실제 로드된 프롬프트 내용 (일부): {}",
                            prompt.substring(0, Math.min(prompt.length(), 200))); // 앞 200자만 로그 출력

                    botAnswer = geminiService.askWithPrompt(prompt, userQuestion);

                } else {
                    log.info("⚡ 프롬프트 매핑 없음 → 일반 대화 모드로 처리");
                    botAnswer = geminiService.ask(history, userQuestion);
                }

                if (botAnswer == null || botAnswer.isBlank()) {
                    botAnswer = "AI 응답이 비어있습니다. 잠시 후 다시 시도해주세요.";
                } else if (botAnswer.length() < 20) {
                    botAnswer += "<br><br>더 자세한 내용이 필요하시면 다시 물어봐주세요!";
                }
            } catch (Exception e) {
                log.error("Gemini API 호출 실패", e);
                botAnswer = " AI 서버 호출에 실패했습니다. 잠시 후 다시 시도해주세요.";
            }

            // 답변 히스토리 추가
            history.add(new GeminiMessageDto(0L, botAnswer));

            // 클라이언트에 응답
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
        // 필요하면 클라이언트에 친절한 메시지 전송
        if (s.isOpen()) {
            s.sendMessage(new TextMessage("⚠️ 서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));
        }
    }


//    로컬 FAQ 응답 (키워드 필터링)
//    금지 / 매너 / 운영 정책 키워드가 포함되면 고정 답변 반환
//    해당 키워드가 없으면 null → Gemini API 호출
    private boolean containsAny(String text, String... keywords) {
        return Arrays.stream(keywords).anyMatch(text::contains);
    }

    private String getLocalFaqAnswer(String question) {
        return FaqLoader.findAnswer(question);
    }

    private String classifyCategory(String userQuestion) {
        String prompt = PromptLoader.buildPrompt("category_classifier.txt", userQuestion);
        return geminiService.askWithPrompt(prompt, userQuestion).trim();
    }
}

