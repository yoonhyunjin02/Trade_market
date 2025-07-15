package com.owl.trade_market.dto;

import java.util.List;

/**
 * 특정 채티방의 상세 정보(상품, 과거 메시지 목록 등)를 담는 DTO
 */
public class ChatRoomDetailDto {

    private Long chatRoomId;
    private String productTitle;
    private Integer productPrice;
    private String productImageUrl;
    private String otherUserName;
    private List<ChatMessageDto> messages; // 과거 메시지 목록

    // 기본 생성자
    public ChatRoomDetailDto() {}

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
}
