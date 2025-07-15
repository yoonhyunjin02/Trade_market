package com.owl.trade_market.dto;

import com.owl.trade_market.entity.Chat;
import com.owl.trade_market.entity.User;

import java.time.format.DateTimeFormatter;

/**
 * 실시간 채팅 메시지를 주고받기 위한 DTO
 */
public class ChatMessageDto {

    private Long chatRoomId;
    private String userId;
    private String assistantId;
    private String content;

    // --- 화면 표시를 위한 추가 정보 ---
    private String senderName;
    private String sentAt;

    public ChatMessageDto() {}

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    // Chat 엔티티와 해당 메시지를 보낸 User 엔티티를 받아 DTO를 생성합니다.
    public static ChatMessageDto fromEntity(Chat chat, User sender) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setChatRoomId(chat.getChatRoom().getId());
        dto.setUserId(chat.getUserId());
        dto.setAssistantId(chat.getAssistantId());
        dto.setContent(chat.getContent());
        dto.setSenderName(sender.getUserName()); // User 객체에서 실제 사용자 이름 설정
        dto.setSentAt(chat.getCreatedAt().format(DateTimeFormatter.ofPattern("a h:mm")));
        return dto;
    }

    // Getters and Setters

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
}
