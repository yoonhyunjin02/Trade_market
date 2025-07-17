package com.owl.trade_market.repository;

import com.owl.trade_market.entity.ChatRoom;
import com.owl.trade_market.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 특정 상품에 대해 특정 구매자가 생성한 채팅방이 있는지 조회
     * 중복 채팅방 생성을 방지
     */
    Optional<ChatRoom> findByProductIdAndBuyerId(Long productId, Long buyerId);
    
    /**
     * 특정 사용자가 구매자 또는 판매자로 참여하고 있는 모든 채팅방 목록을 최신순으로 조회
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.buyer = :user OR cr.product.seller = :user ORDER BY cr.id DESC")
    List<ChatRoom> findChatRoomsInvolvingUser(@Param("user") User user);
}
