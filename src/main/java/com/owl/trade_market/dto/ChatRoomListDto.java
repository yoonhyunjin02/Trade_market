package com.owl.trade_market.dto;

import com.owl.trade_market.entity.Chat;
import com.owl.trade_market.entity.ChatRoom;
import com.owl.trade_market.entity.User;

import java.time.format.DateTimeFormatter;

/**
 * 채팅방 목록의 각 아이템을 표현하기 위한 DTO
 */
public class ChatRoomListDto {

    private Long chatRoomId;
    private String otherUserName;
    private String lastMessage;
    private String lastMessageTime;
    private String productImageUrl;
    private int unreadCount;

    // 기본 생성자
    public ChatRoomListDto() {}

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static ChatRoomListDto fromEntity(ChatRoom chatRoom, Chat lastChat, User currentUser) {
        // 채팅 상대방 찾기
        User otherUser;

        // 현재 사용자가 구매자면, 상대방은 판매자
        if (chatRoom.getBuyer().getId().equals(currentUser.getId())) {
            otherUser = chatRoom.getProduct().getSeller();
        } else { // 현재 사용자가 판매자이면, 상대방은 구매자
            otherUser = chatRoom.getBuyer();
        }

        // 마지막 메시지 정보 설정
        String lastMessageContent = "대화를 시작해보세요.";
        String lastMessageSentAt = "";
        if (lastChat != null) {
            lastMessageContent = lastChat.getContent();
            lastMessageSentAt = lastChat.getCreatedAt().format(DateTimeFormatter.ofPattern("a h:mm"));
        }
        
        // 상품 대표 이미지 URL 설정
        String imageUrl = "/static/images/mascot.png";
        if (chatRoom.getProduct().getImages() != null && !chatRoom.getProduct().getImages().isEmpty()) {
            imageUrl = chatRoom.getProduct().getImages().get(0).getImage();
        }

        // DTO 생성 및 반환
        ChatRoomListDto dto = new ChatRoomListDto();
        dto.setChatRoomId(chatRoom.getId());
        dto.setOtherUserName(otherUser.getUserName());
        dto.setLastMessage(lastMessageContent);
        dto.setLastMessageTime(lastMessageSentAt);
        dto.setProductImageUrl(imageUrl);

        return dto;
    }

    // Getter and Setter

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
