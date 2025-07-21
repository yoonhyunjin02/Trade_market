package com.owl.trade_market.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.trade_market.dto.ChatMessageDto;
import com.owl.trade_market.entity.Chat;
import com.owl.trade_market.entity.ChatRoom;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    // 사용자 ID를 키로, WebSocketSession을 값으로 저장하여 특정 사용자에게 메시지를 보낼 수 있게 합니다.
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 채팅방별 연결된 세션들을 관리 (채팅방 ID -> 사용자 ID 세트)
    private final Map<Long, Map<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // 생성자 주입
    @Autowired
    public ChatWebSocketHandler(ObjectMapper objectMapper, ChatService chatService) {
        this.objectMapper = objectMapper;
        this.chatService = chatService;
    }

    /**
     * 클라이언트와 연결이 수립되었을 때 호출됩니다.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // WebSocketSession에서 현재 로그인된 사용자 정보를 가져옵니다.
        String userId = getUserId(session);

        if (userId == null) {
            log.warn("사용자 ID를 찾을 수 없어 연결을 종료합니다. Session: {}", session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("User ID not found"));
            return;
        }

        sessions.put(userId, session);
        log.info("새로운 클라이언트 연결: 사용자 ID={}, 세션 ID={}", userId, session.getId());
    }

    /**
     * 클라이언트로부터 텍스트 메시지를 수신했을 때 호출됩니다.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("수신 메시지: {}", payload);

        try {
            // 수신한 JSON 메시지를 ChatMessageDto 객체로 변환
            ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);

            // 메시지 유효성 검사
            if (chatMessageDto.getChatRoomId() == null ||
                    chatMessageDto.getUserId() == null ||
                    chatMessageDto.getContent() == null || chatMessageDto.getContent().trim().isEmpty()) {
                log.warn("잘못된 메시지 형식: {}", payload);
                sendErrorMessage(session, "잘못된 메시지 형식입니다.");
                return;
            }

            // 사용자 권한 검증
            String sessionUserId = getUserId(session);
            if (!chatMessageDto.getUserId().equals(sessionUserId)) {
                log.warn("사용자 ID 불일치: 세션={}, 메시지={}", sessionUserId, chatMessageDto.getUserId());
                sendErrorMessage(session, "권한이 없습니다.");
                return;
            }

            // 수신한 메시지를 DB에 저장
            Chat savedChat = chatService.saveMessage(chatMessageDto);

            // 저장된 메시지로 응답 DTO 생성
            User sender = chatService.findUserById(chatMessageDto.getUserId());
            ChatMessageDto responseDto = ChatMessageDto.fromEntity(savedChat, sender);

            // 채팅방 정보를 조회하여 참여자들을 찾습니다.
            ChatRoom chatRoom = chatService.findRoomById(chatMessageDto.getChatRoomId());

            // 채팅방의 모든 참여자에게 메시지 브로드캐스트
            broadcastToRoom(chatRoom, responseDto);

            log.info("메시지 저장 및 브로드캐스트 완료: 채팅방 ID={}, 발신자={}",
                    chatRoom.getId(), sender.getUserId());

        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
            sendErrorMessage(session, "메시지 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 클라이언트와 연결이 종료되었을 때 호출됩니다.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserId(session);
        if (userId != null) {
            // 연결이 종료된 사용자의 세션을 맵에서 제거합니다.
            sessions.remove(userId);

            // 모든 채팅방에서 해당 사용자 세션 제거
            roomSessions.values().forEach(roomSessionMap -> roomSessionMap.remove(userId));

            log.info("클라이언트 연결 종료: 사용자 ID={}, 세션 ID={}, 상태={}",
                    userId, session.getId(), status);
        }
    }

    /**
     * WebSocket 오류 발생 시 호출됩니다.
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String userId = getUserId(session);
        log.error("WebSocket 전송 오류 발생: 사용자 ID={}, 세션 ID={}, 오류={}",
                userId, session.getId(), exception.getMessage(), exception);

        // 오류 발생 시 연결 정리
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    // --- Helper Methods ---

    /**
     * 특정 채팅방의 모든 참여자에게 메시지를 브로드캐스트
     */
    private void broadcastToRoom(ChatRoom chatRoom, ChatMessageDto message) {
        try {
            // ChatService를 통해 안전하게 사용자 정보 조회 (트랜잭션 내에서)
            String buyerId = chatService.getBuyerIdFromChatRoom(chatRoom.getId());
            String sellerId = chatService.getSellerIdFromChatRoom(chatRoom.getId());

            // 구매자에게 전송
            sendMessageToUser(buyerId, message);

            // 판매자에게 전송
            sendMessageToUser(sellerId, message);

        } catch (Exception e) {
            log.error("브로드캐스트 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 특정 사용자에게 메시지 전송
     */
    private void sendMessageToUser(String userId, ChatMessageDto message) {
        WebSocketSession userSession = sessions.get(userId);
        if (userSession != null && userSession.isOpen()) {
            sendMessage(userSession, message);
        } else {
            log.debug("사용자 {}가 오프라인 상태입니다.", userId);
        }
    }

    /**
     * 특정 세션에 메시지를 보내는 헬퍼 메서드
     */
    private void sendMessage(WebSocketSession session, ChatMessageDto message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
            log.debug("메시지 전송 완료: 받는사람={}, 내용={}",
                    getUserId(session), message.getContent());
        } catch (IOException e) {
            log.error("메시지 전송 실패: 세션 ID={}, 오류={}", session.getId(), e.getMessage());

            // 전송 실패한 세션은 연결 정리
            String userId = getUserId(session);
            if (userId != null) {
                sessions.remove(userId);
            }
        }
    }

    /**
     * 오류 메시지를 클라이언트에게 전송
     */
    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            Map<String, Object> errorResponse = Map.of(
                    "type", "error",
                    "message", errorMessage
            );
            String jsonError = objectMapper.writeValueAsString(errorResponse);
            session.sendMessage(new TextMessage(jsonError));
        } catch (IOException e) {
            log.error("오류 메시지 전송 실패: 세션 ID={}, 오류={}", session.getId(), e.getMessage());
        }
    }

    /**
     * WebSocketSession에서 사용자 ID를 추출하는 헬퍼 메서드
     */
    private String getUserId(WebSocketSession session) {
        // Spring Security와 연동된 Principal에서 사용자 이름을 가져옵니다.
        if (session.getPrincipal() != null) {
            return session.getPrincipal().getName();
        }

        // Principal이 없는 경우를 대비한 대체 로직
        Map<String, Object> attributes = session.getAttributes();
        if (attributes.get("user") instanceof User) {
            return ((User) attributes.get("user")).getUserId();
        }

        Object userId = attributes.get("userId");
        return userId != null ? userId.toString() : null;
    }
}