package com.owl.trade_market.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        // 로그인 유저 정보 추출
        Principal principal = request.getPrincipal();

        if (principal != null) {
            // 예: SecurityContextHolder에 저장된 username 사용
            // Spring Security 기본 principal → username
            String username = principal.getName();

            // WebSocket 세션 Attributes에 userId 저장
            attributes.put("userId", username);

            System.out.println("WebSocket Handshake - 로그인 사용자: " + username);
        } else {
            // 비로그인 사용자는 guest로 처리
            attributes.put("userId", "guest");
            System.out.println("WebSocket Handshake - 게스트 접속");
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }
}
