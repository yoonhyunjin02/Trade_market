package com.owl.trade_market.service;

import com.owl.trade_market.dto.ChatMessageDto;
import com.owl.trade_market.dto.ChatRoomDetailDto;
import com.owl.trade_market.dto.ChatRoomListDto;
import com.owl.trade_market.entity.Chat;
import com.owl.trade_market.entity.ChatRoom;
import com.owl.trade_market.entity.User;

import java.util.List;

public interface ChatService {

    // --- 채팅방 목록 조회 관련 ---
    /**
     * 현재 로그인한 사용자가 참여하고 있는 모든 채팅방 목록을 조회합니다.
     * 각 채팅방의 마지막 메시지, 상대방 정보, '안 읽은 메시지 수' 를 포함합니다.
     */
    List<ChatRoomListDto> findRoomsForUser(User currentUser);

    /**
     * 현재 사용자가 참여한 채팅방 중, 상대방이 보낸 '읽지 않은 메시지'가 하나 이상 있는
     * 채팅방 목록만 조회합니다.
     */
    List<ChatRoomListDto> findUnreadRoomsForUser(User currentUser);

    // --- 메시지 상태 처리 관련 ---
    /**
     * 클라이언트로부터 받은 메시지 DTO를 Chat 엔티티로 변환하여 DB에 저장합니다.
     */
    Chat saveMessage(ChatMessageDto messageDto);

    /**
     * 읽지 않은 메시지를 '읽음' 상태로 변경합니다.
     * 사용자가 채팅방에 입장할 때 호출됩니다.
     */
    void markMessagesAsRead(Long chatRoomId, User currentUser);

    // --- 내부 로직 처리를 위한 조회 메서드 ---
    /**
     * ID로 특정 채팅방 엔티티를 조회합니다.
     */
    ChatRoom findRoomById(Long roomId);

    /**
     * ID로 특정 사용자 엔티티를 조회합니다.
     */
    User findUserById(String userId);

    /**
     * 특정 채팅방의 상세 정보(상품 정보, 상대방 정보, 과거 메시지 목록)를 조회합니다.
     */
    ChatRoomDetailDto findRoomDetails(Long roomId, User currentUser);

    /**
     * 특정 상품에 대해 구매자와 판매자 간의 채팅방을 찾거나 새로 생성합니다.
     */
    ChatRoom findOrCreateRoom(Long productId, User buyer);

    /**
     * 채팅방 ID로 구매자 ID를 조회합니다.
     */
    String getBuyerIdFromChatRoom(Long chatRoomId);

    /**
     * 채팅방 ID로 판매자 ID를 조회합니다.
     */
    String getSellerIdFromChatRoom(Long chatRoomId);

    /**
     * 채팅방 나가기 및 완전 삭제
     */
    void leaveAndDeleteRoom(Long chatRoomId, User currentUser);
}
