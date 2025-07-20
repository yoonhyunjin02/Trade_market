package com.owl.trade_market.repository;

import com.owl.trade_market.entity.Chat;
import com.owl.trade_market.entity.ChatRoom;
import com.owl.trade_market.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // 읽지 않은 메시지 수 카운트
    int countByChatRoomIdAndUserIdNotAndIsReadIsFalse(Long chatRoomId, String currentUserId);

    //상대방이 보낸 모든 메시지를 읽음으로 처리
    @Modifying
    @Transactional
    @Query("UPDATE Chat c SET c.isRead = true WHERE c.chatRoom.id = :chatRoomId AND c.userId != :currentUserId AND c.isRead = false")
    void markAsReadByRoomIdAndUserId(@Param("chatRoomId") Long chatRoomId, @Param("currentUserId") String currentUserId);

    // 읽지 않은 메시지가 있는 채팅방 ID 목록 조회
    @Query("SELECT DISTINCT c.chatRoom.id FROM Chat c WHERE (c.chatRoom.buyer = :user OR c.chatRoom.product.seller = :user) AND c.isRead = false AND c.userId != :userId")
    List<Long> findChatRoomIdsWithUnreadMessagesForUser(@Param("user") User user, @Param("userId") String userId);

    // 특정 채팅방의 마지막 메시지 1건을 조회합니다.
    Optional<Chat> findTopByChatRoomOrderByIdDesc(ChatRoom chatroom);
}
