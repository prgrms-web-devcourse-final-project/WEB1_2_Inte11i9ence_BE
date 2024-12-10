package com.prgrmsfinal.skypedia.chat.service;

import com.prgrmsfinal.skypedia.chat.dto.ChatMessageDTO;
import com.prgrmsfinal.skypedia.chat.dto.ChatRoomDTO;
import com.prgrmsfinal.skypedia.chat.entity.ChatMessage;
import com.prgrmsfinal.skypedia.chat.entity.ChatRoom;
import com.prgrmsfinal.skypedia.chat.entity.ChatRoomStatus;
import com.prgrmsfinal.skypedia.chat.repository.ChatMessageRepository;
import com.prgrmsfinal.skypedia.chat.repository.ChatRoomRepository;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ChatRoomDTO createChatRoom(Long creatorId, Long participantId) {
        // 이미 존재하는 채팅방 확인
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByCreatorIdAndParticipantIdOrParticipantIdAndCreatorId(
                creatorId, participantId, creatorId, participantId);

        // 이미 존재하는 채팅방이 있다면
        if (existingRoom.isPresent()) {
            ChatRoom chatRoom = existingRoom.get();
            // 채팅방이 차단된 상태라면 예외 발생
            if (chatRoom.getStatus() == ChatRoomStatus.BLOCKED) {
                throw new IllegalStateException("Blocked chat room");
            }
            // 채팅방이 삭제된 상태라면 채팅방과 메시지들을 삭제
            if (chatRoom.getStatus() == ChatRoomStatus.DELETED) {
                // 채팅방에 속한 모든 메시지 삭제
                chatMessageRepository.deleteAllByChatRoomId(chatRoom.getId());
                // 채팅방 삭제
                chatRoomRepository.delete(chatRoom);
            }
        }

        // 새로운 채팅방 생성
        Member creator = memberRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Creator not found"));
        Member participant = memberRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        ChatRoom chatRoom = ChatRoom.builder()
                .creator(creator)
                .participant(participant)
                .status(ChatRoomStatus.ACTIVE)
                .build();

        return ChatRoomDTO.from(chatRoomRepository.save(chatRoom));
    }

    @Transactional(readOnly = true)
    public Page<ChatRoomDTO> getChatRooms(Long userId, Pageable pageable) {
        return chatRoomRepository.findActiveChatRooms(userId, pageable)
                .map(ChatRoomDTO::from);
    }

    public ChatMessageDTO saveMessage(ChatMessageDTO messageDTO) {
        ChatRoom chatRoom = chatRoomRepository.findById(messageDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        Member sender = memberRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));

        if (!chatRoom.canSendMessage(sender)) {
            throw new IllegalArgumentException("Cannot send message in this room");
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(messageDTO.getContent())
                .build();

        return ChatMessageDTO.from(chatMessageRepository.save(message));
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageDTO> getChatMessages(Long roomId, Long userId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        if (!chatRoom.isMember(memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")))) {
            throw new IllegalArgumentException("User is not in this chat room");
        }

        return chatMessageRepository.findByChatRoomId(roomId, pageable)
                .map(ChatMessageDTO::from);
    }

    public void markMessagesAsRead(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!chatRoom.isMember(user)) {
            throw new IllegalArgumentException("User is not in this chat room");
        }

        chatMessageRepository.findByChatRoomId(roomId, Pageable.unpaged())
                .forEach(message -> {
                    if (!message.getSender().equals(user)) {
                        message.markAsRead();
                    }
                });
    }

    public void leaveRoom(Long userId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        chatRoom.leaveRoom(user);
    }

    public void blockUser(Long userId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        chatRoom.blockUser(user);
    }
}