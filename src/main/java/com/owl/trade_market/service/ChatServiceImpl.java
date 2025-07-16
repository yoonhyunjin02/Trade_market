package com.owl.trade_market.service;

import com.owl.trade_market.dto.ChatMessageDto;
import com.owl.trade_market.dto.ChatRoomListDto;
import com.owl.trade_market.entity.Chat;
import com.owl.trade_market.entity.ChatRoom;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.repository.ChatRepository;
import com.owl.trade_market.repository.ChatRoomRepository;
import com.owl.trade_market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository, ChatRoomRepository chatRoomRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
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
}
