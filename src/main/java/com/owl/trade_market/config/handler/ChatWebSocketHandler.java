package com.owl.trade_market.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owl.trade_market.dto.ChatMessageDto;
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

    // 사용자 ID를 키로, WebSocketSession을 값으로 저장하여 특정 사용자에게 메세지를 보낼 수 있게 합니다.
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

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
            log.warn("사용자 ID를 찾을 수 없어 연결을 종료합니다.");
            session.close(CloseStatus.BAD_DATA.withReason("User ID not found"));
            return;
        }

        sessions.put(userId, session);
        log.info("새로운 클라이언트 연결: 사용자 ID={}", userId);
    }

    /**
     * 클라이언트로부터 텍스트 메시지를 수신했을 때 호출됩니다.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("수신 메시지: {}", payload);

        //수신한 JSON 메시지를 ChatMessageDto 객체로 변환
        ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
        
        // 수신한 메세지를 DB에 저장
        // 이 과정에서 Chat 엔티티가 생성되고 저장됩니다.
        chatService.saveMessage(chatMessageDto);

        // 채팅방 정보를 조회하여 상대방을 찾습니다.
        ChatRoom chatRoom = chatService.findRoomById(chatMessageDto.getChatRoomId());
        User sender = chatService.findUserById(chatMessageDto.getUserId());
        
        // 상대방 ID를 결정
        // 현재 사용자가 구매자이면 상대방은 판매자, 판매자이면 상대방은 구매자
        User opponent;
        if (chatRoom.getBuyer().getId().equals(sender.getId())) {
            opponent = chatRoom.getProduct().getSeller();
        } else {
            opponent = chatRoom.getBuyer();
        }
        
        // 상대방이 현재 접속 중(세션이 존재)라면 메세지를 전송합니다.
        WebSocketSession opponentSession = sessions.get(opponent.getUserId());
        if (opponentSession != null && opponentSession.isOpen()) {
            sendMessage(opponentSession, chatMessageDto);
        } else {
            // 상대방이 오프라인일 때의 처리
            // 메시지는 이미DB에 '읽지 않음' 상태로 저장되었다.
            log.info("상대방({})이 오프라인 상태입니다.", opponent.getUserId());
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
            log.info("클라이언트 연결 종료: 사용자 ID = {}, 상태 = {}", userId, status);
        }
    }
    
    // --- Helper Methods ---

    /**
     * 특정 세션에 메세지를 보내는 헬퍼 메서드
     */
    private void sendMessage(WebSocketSession session, ChatMessageDto message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
            log.info("메시지 전송 완료: 받는사람 = {}, 내용 = {}", getUserId(session), message.getContent());
        } catch (IOException e) {
            log.error("메시지 전송 실패, 세션 ID: {}, 오류: {}", session.getId(), e.getMessage());
        }
    }

    /**
     * WebSocketSession에서 사용자 ID를 추출하는 헬퍼 메서드
     * 채팅방에서 누가 메시지를 보냈는지, 어떤 세션이 누구인지 식별을 위한 메서드
     */
    private String getUserId(WebSocketSession session) {
        // Spring Security와 연동된 Principal에서 사용자 이름을 가져옵니다.
        if (session.getPrincipal() != null) {
            return session.getPrincipal().getName();
        }

        //Principal이 없는 경우를 대비한 대체 로직
        Map<String, Object> attributes = session.getAttributes();
        if (attributes.get("user") instanceof User) {
            return ((User) attributes.get("user")).getUserId();
        }
        return (String) attributes.get("userId");
    }
}
