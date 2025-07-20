package com.owl.trade_market.config;

import com.owl.trade_market.config.handler.ChatBotWebSocketHandler;
import com.owl.trade_market.config.handler.ChatWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // WebSocket 서버 기능 활성화
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ChatBotWebSocketHandler chatBotWebSocketHandler;

    // 생성자 주입
    @Autowired
    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler,
                           ChatBotWebSocketHandler chatBotWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.chatBotWebSocketHandler = chatBotWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .addInterceptors(new WsHandshakeInterceptor())
                .setAllowedOrigins("*");

        registry.addHandler(chatBotWebSocketHandler, "/ws/chatbot")
                .addInterceptors(new WsHandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}