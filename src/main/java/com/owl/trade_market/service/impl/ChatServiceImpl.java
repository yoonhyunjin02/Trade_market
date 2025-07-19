package com.owl.trade_market.service.impl;

import com.owl.trade_market.dto.ChatMessageDto;
import com.owl.trade_market.dto.ChatRoomDetailDto;
import com.owl.trade_market.dto.ChatRoomListDto;
import com.owl.trade_market.entity.Chat;
import com.owl.trade_market.entity.ChatRoom;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.repository.ChatRepository;
import com.owl.trade_market.repository.ChatRoomRepository;
import com.owl.trade_market.repository.ProductRepository;
import com.owl.trade_market.repository.UserRepository;
import com.owl.trade_market.service.ChatService;
import com.owl.trade_market.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository, ChatRoomRepository chatRoomRepository, UserRepository userRepository, ProductRepository productRepository, ProductService productService) {
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Override
    public List<ChatRoomListDto> findRoomsForUser(User currentUser) {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsInvolvingUser(currentUser);
        return chatRooms.stream()
                .map(room -> convertToDto(room, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomListDto> findUnreadRoomsForUser(User currentUser) {
        // 현재 유저의 읽지 않은 메시지가 있는 채팅방 ID 목록을 가져옴
        List<Long> roomIds = chatRepository.findChatRoomIdsWithUnreadMessagesForUser(currentUser, currentUser.getUserId());
        if (roomIds.isEmpty()) {
            return Collections.emptyList();
        }

        // ID 목록으로 채팅방들을 조회
        List<ChatRoom> chatRooms = chatRoomRepository.findAllById(roomIds);

        // DTO로 변환
        return chatRooms.stream()
                .map(room -> convertToDto(room, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Chat saveMessage(ChatMessageDto messageDto) {
        ChatRoom chatRoom = findRoomById(messageDto.getChatRoomId());
        User sender = findUserById(messageDto.getUserId());

        Chat chat = new Chat();
        chat.setUserId(sender.getUserId());
        chat.setContent(messageDto.getContent());
        chat.setAssistantId(messageDto.getAssistantId());

        // 연관관계 편의 메서드 사용
        chatRoom.addChat(chat);

        return chatRepository.save(chat);
    }

    /**
     *  메시지 읽음으로 표시하는 메서드
     */
    @Override
    @Transactional
    public void markMessagesAsRead(Long chatRoomId, User currentUser) {
        chatRepository.markAsReadByRoomIdAndUserId(chatRoomId, currentUser.getUserId());
    }

    @Override
    public ChatRoom findRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다. ID: " + roomId));
    }

    @Override
    public User findUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
    }

    /**
     * ChatRoom 엔티티를 ChatRoomListDto로 변환하는 헬퍼 메서드
     */
    private ChatRoomListDto convertToDto(ChatRoom room, User currentUser) {
        Chat lastChat = chatRepository.findTopByChatRoomOrderByIdDesc(room).orElse(null);
        int unreadCount = chatRepository.countByChatRoomIdAndUserIdNotAndIsReadIsFalse(room.getId(), currentUser.getUserId());
        ChatRoomListDto dto = ChatRoomListDto.fromEntity(room, lastChat, currentUser);
        dto.setUnreadCount(unreadCount);

        return dto;
    }

    @Override
    public ChatRoomDetailDto findRoomDetails(Long roomId, User currentUser) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다. ID: " + roomId));

        // 권한 체크: 현재 유저가 채팅방의 참여자인지 확인
        boolean isParticipant = room.getBuyer().getId().equals(currentUser.getId()) ||
                room.getProduct().getSeller().getId().equals(currentUser.getId());
        if (!isParticipant) {
            throw new AccessDeniedException("채팅방에 접근할 권한이 없습니다.");
        }

        // 상대방 찾기
        User opponent = room.getBuyer().getId().equals(currentUser.getId())
                ? room.getProduct().getSeller()
                : room.getBuyer();

        return ChatRoomDetailDto.fromEntity(room, opponent, room.getChats());

    }

    @Override
    @Transactional
    public ChatRoom findOrCreateRoom(Long productId, User buyer) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        if (product.getSeller().getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("자신의 상품에는 채팅을 할 수 없습니다.");
        }

        // 기존 채팅방 확인, 없으면 새로 생성
        return chatRoomRepository.findByProductIdAndBuyerId(productId, buyer.getId())
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setProduct(product);
                    newRoom.setBuyer(buyer);
                    productService.increaseViewCount(productId); // 상품의 채팅 카운트 증가
                    return chatRoomRepository.save(newRoom);
                });
    }

    @Override
    public String getBuyerIdFromChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다. ID: " + chatRoomId));
        return chatRoom.getBuyer().getUserId();
    }

    @Override
    public String getSellerIdFromChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다. ID: " + chatRoomId));
        return chatRoom.getProduct().getSeller().getUserId();
    }

    @Override
    @Transactional
    public void leaveAndDeleteRoom(Long chatRoomId, User currentUser) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        // 사용자가 해당 채팅방의 참여자인지 확인
        boolean isBuyer = chatRoom.getBuyer().getId().equals(currentUser.getId());
        boolean isSeller = chatRoom.getProduct().getSeller().getId().equals(currentUser.getId());

        if (!isBuyer && !isSeller) {
            throw new AccessDeniedException("해당 채팅방에 참여하지 않은 사용자입니다.");
        }

        try {
            // CASCADE 설정에 따라 채팅 메시지들도 함께 삭제됨
            chatRoomRepository.deleteById(chatRoomId);

            log.info("채팅방 및 관련 메시지 삭제 완료 - rooId: {}, userId: {}", chatRoomId, currentUser.getUserId());
        } catch (Exception e) {
            log.error("채팅방 삭제 실패 - roomId: {}, userId: {}, error: {}", chatRoomId, currentUser.getUserId(), e.getMessage());
            throw new RuntimeException("채팅방 삭제 중 오류가 발생했습니다. 다시 시도해주세요.", e);
        }
    }
}
