package com.owl.trade_market.dto;

public class GeminiMessageDto {
    private Long userId;
    private String content;

    public GeminiMessageDto() {
    }

    public GeminiMessageDto(Long userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
