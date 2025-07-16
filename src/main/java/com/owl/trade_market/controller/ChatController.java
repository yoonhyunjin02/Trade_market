package com.owl.trade_market.controller;

import com.owl.trade_market.dto.ChatRoomDetailDto;
import com.owl.trade_market.dto.ChatRoomListDto;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.security.CustomUserDetails;
import com.owl.trade_market.service.ChatService;
import com.owl.trade_market.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    // 생성자 주입
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping
    public String chatPage(Model model, Authentication authentication) {

        String userId = null;
        Object principal = authentication.getPrincipal();

        // SSO 사용자인지, LOCAL 사용자인지 확인
        if (principal instanceof CustomUserDetails customUser) {
            userId = customUser.getUsername(); // LOCAL
        } else if (principal instanceof OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("user_email");
            if (email != null && email.contains("@")) {
                userId = email.substring(0, email.indexOf('@'));
            }
        }

        User currentUser = userService.findByUserId(userId).get();

        // 전체 채팅방 목록
        List<ChatRoomListDto> allChatRooms = chatService.findRoomsForUser(currentUser);

        // 읽지 않은 메시지가 있는 채팅방 목록
        List<ChatRoomListDto> unreadChatRooms = chatService.findUnreadRoomsForUser(currentUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allChatRooms", allChatRooms);
        model.addAttribute("unreadChatRooms", unreadChatRooms);

        return "/pages/chat";
    }

    /**
     * 채팅방 상세 정보 (과거 메시지 목록, 상품 정보)를 조회
     */
    @GetMapping("/api/chats/{roomId}")
    @ResponseBody
    public ResponseEntity<ChatRoomDetailDto> getChatRoomDetails(@PathVariable Long roomId, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ChatRoomDetailDto roomDetails = chatService.findRoomDetails(roomId, currentUser);
        return ResponseEntity.ok(roomDetails);
    }

    /**
     * 특정 채팅방의 메시지를 모두 '읽음'으로 처리
     */
    @PostMapping("/api/chats/{roomId}/read")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(@PathVariable Long roomId, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        chatService.markMessagesAsRead(roomId, currentUser);
        return ResponseEntity.ok().build();
    }

}
