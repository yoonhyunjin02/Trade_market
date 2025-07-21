package com.owl.trade_market.dto;

import com.owl.trade_market.entity.Chat;
import com.owl.trade_market.entity.ChatRoom;
import com.owl.trade_market.entity.User;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 특정 채팅방의 상세 정보(상품, 과거 메시지 목록 등)를 담는 DTO
 */
public class ChatRoomDetailDto {

    private Long chatRoomId;
    private String productTitle;
    private Integer productPrice;
    private String formattedPrice; // 화면 표시용 포맷된 가격(12,000)
    private String productImageUrl;
    private String otherUserName;
    private List<ChatMessageDto> messages; // 과거 메시지 목록
    private boolean completed; // 채팅방이 완료된 상태인지 여부
    private String sellerId; // 판매자(상품 등록자)의 userId

    // private 생성자
    // 외부에서 직접 생성자 호출 불가
    // fromEntity()를 사용해서 객체 생성 유도
    private ChatRoomDetailDto() {
    }

    // 기본 생성자
    public ChatRoomDetailDto(Long chatRoomId, String productTitle, Integer productPrice, String formattedPrice, String productImageUrl, String otherUserName, List<ChatMessageDto> messages) {
        this.chatRoomId = chatRoomId;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.formattedPrice = formattedPrice;
        this.productImageUrl = productImageUrl;
        this.otherUserName = otherUserName;
        this.messages = messages;
    }

    // Getter and Setter
    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }

    public void setFormattedPrice(String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public List<ChatMessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageDto> messages) {
        this.messages = messages;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static ChatRoomDetailDto fromEntity(ChatRoom room, User opponent, List<Chat> chats) {
        ChatRoomDetailDto dto = new ChatRoomDetailDto();

        // 상품 대표 이미지 URL 설정
        String imageUrl = "/static/images/mascot.png";
        if (room.getProduct().getImages() != null && !room.getProduct().getImages().isEmpty()) {
            imageUrl = room.getProduct().getImages().get(0).getImage();
        }

        // 가격 포맷팅
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
        String formatted = numberFormat.format(room.getProduct().getPrice()) + "원";

        dto.setChatRoomId(room.getId());
        dto.setProductTitle(room.getProduct().getTitle());
        dto.setProductPrice(room.getProduct().getPrice());
        dto.setFormattedPrice(formatted);
        dto.setProductImageUrl(imageUrl);
        dto.setOtherUserName(opponent.getUserName());
        dto.setCompleted(room.getProduct().isSold());
        dto.setSellerId(room.getProduct().getSeller().getUserId());

        // 채팅 메시지 엔티티 리스트를 DTO 리스트로 변환
        if (chats != null) {
            List<ChatMessageDto> messageDtos = chats.stream()
                    .map(chat -> {
                        User sender = chat.getUserId().equals(room.getBuyer().getUserId())
                                ? room.getBuyer()
                                : room.getProduct().getSeller();

                        return ChatMessageDto.fromEntity(chat, sender);
                    }).toList();
            dto.setMessages(messageDtos);
        } else {
            dto.setMessages(Collections.emptyList());
        }

        return dto;
    }
}
