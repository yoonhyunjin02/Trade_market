package com.owl.trade_market.controller;

import com.owl.trade_market.dto.ChatMessageDto;
import com.owl.trade_market.dto.ChatRoomDetailDto;
import com.owl.trade_market.dto.ChatRoomListDto;
import com.owl.trade_market.entity.Chat;
import com.owl.trade_market.entity.ChatRoom;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.security.CustomUserDetails;
import com.owl.trade_market.service.ChatService;
import com.owl.trade_market.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    // 생성자 주입
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    /**
     * 채팅 메인 페이지 - 전체 채팅방 목록 표시
     */
    @GetMapping("/chats")
    public String chatPage(Model model, Authentication authentication) {
        // 로그인하지 않은 사용자는 로그인 페이지로 리다이렉트
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            return "redirect:/login";
        }

        User currentUser = getCurrentUserOrThrow(authentication);

        // 전체 채팅방 목록
        List<ChatRoomListDto> allChatRooms = chatService.findRoomsForUser(currentUser);

        // 읽지 않은 메시지가 있는 채팅방 목록
        List<ChatRoomListDto> unreadChatRooms = chatService.findUnreadRoomsForUser(currentUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allChatRooms", allChatRooms);
        model.addAttribute("unreadChatRooms", unreadChatRooms);
        model.addAttribute("selectedChatRoom", null); // 선택된 채팅방 없음

        return "pages/chat";
    }

    /**
     * 특정 채팅방 선택해서 채팅 페이지 표시
     */
    @GetMapping("/chats/{roomId}")
    public String chatPageWithRoom(@PathVariable Long roomId,
                                   Model model,
                                   Authentication authentication) {
        // 로그인 체크
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            return "redirect:/login";
        }

        User currentUser = getCurrentUserOrThrow(authentication);

        // 전체 채팅방 목록
        List<ChatRoomListDto> allChatRooms = chatService.findRoomsForUser(currentUser);
        List<ChatRoomListDto> unreadChatRooms = chatService.findUnreadRoomsForUser(currentUser);

        // 선택된 채팅방 상세 정보
        ChatRoomDetailDto selectedChatRoom = null;
        try {
            selectedChatRoom = chatService.findRoomDetails(roomId, currentUser);
            // 해당 채팅방의 메시지를 읽음 처리
            chatService.markMessagesAsRead(roomId, currentUser);
        } catch (Exception e) {
            // 채팅방 접근 권한이 없거나 존재하지 않는 경우
            return "redirect:/chats";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allChatRooms", allChatRooms);
        model.addAttribute("unreadChatRooms", unreadChatRooms);
        model.addAttribute("selectedChatRoom", selectedChatRoom);

        return "pages/chat";
    }

    /**
     * 특정 상품에 대해 채팅방을 생성하거나 기존 채팅방으로 이동합니다.
     */
    @PostMapping("/chats/create")
    public String createChatRoom(@RequestParam Long productId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            redirectAttributes.addFlashAttribute("error", "채팅을 시작하려면 로그인이 필요합니다.");
            return "redirect:/login";
        }

        try {
            User currentUser = getCurrentUserOrThrow(authentication);
            ChatRoom chatRoom = chatService.findOrCreateRoom(productId, currentUser);

            // 생성되거나 찾은 채팅방으로 이동
            return "redirect:/chats/" + chatRoom.getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/" + productId;
        }
    }

    /**
     * 메시지 전송 API
     */
    @PostMapping("/api/chats/{roomId}/messages")
    @ResponseBody
    public ResponseEntity<ChatMessageDto> sendMessage(@PathVariable Long roomId,
                                                      @RequestBody ChatMessageDto messageDto,
                                                      Authentication authentication) {

        User currentUser = getCurrentUserOrThrow(authentication);

        // DTO에 필요한 정보 설정
        messageDto.setChatRoomId(roomId);
        messageDto.setUserId(currentUser.getUserId());

        // 메시지 저장
        Chat savedChat = chatService.saveMessage(messageDto);

        // 응답 DTO 생성
        ChatMessageDto responseDto = ChatMessageDto.fromEntity(savedChat, currentUser);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 채팅방 상세 정보 (과거 메시지 목록, 상품 정보)를 조회
     */
    @GetMapping("/api/chats/{roomId}")
    @ResponseBody
    public ResponseEntity<ChatRoomDetailDto> getChatRoomDetails(@PathVariable Long roomId,
                                                                Authentication authentication) {

        User currentUser = getCurrentUserOrThrow(authentication);
        ChatRoomDetailDto roomDetails = chatService.findRoomDetails(roomId, currentUser);
        return ResponseEntity.ok(roomDetails);
    }

    /**
     * 특정 채팅방의 메시지를 모두 '읽음'으로 처리
     */
    @PostMapping("/api/chats/{roomId}/read")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(@PathVariable Long roomId, Authentication authentication) {
        User currentUser = getCurrentUserOrThrow(authentication);

        chatService.markMessagesAsRead(roomId, currentUser);
        return ResponseEntity.ok().build();
    }

    /**
     * Authentication 객체에서 User 엔티티를 가져오는 헬퍼 메서드
     */
    private User getCurrentUserOrThrow(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUser) {
            return customUser.getUser();
        } else if (principal instanceof OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");
            String userId = email != null && email.contains("@") ? email.substring(0, email.indexOf('@')) : null;
            return userService.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("OAuth2 인증 사용자를 찾을 수 없습니다."));
        }

        throw new IllegalStateException("지원하지 않는 인증 타입: " + principal.getClass().getName());
    }
}