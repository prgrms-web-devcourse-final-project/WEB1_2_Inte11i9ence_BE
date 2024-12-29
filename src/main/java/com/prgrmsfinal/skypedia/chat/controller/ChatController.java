package com.prgrmsfinal.skypedia.chat.controller;

import com.prgrmsfinal.skypedia.chat.dto.ChatMessageDTO;
import com.prgrmsfinal.skypedia.chat.dto.ChatRoomDTO;
import com.prgrmsfinal.skypedia.chat.service.ChatService;
import com.prgrmsfinal.skypedia.oauth2.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    @PostMapping("/room")
    public ResponseEntity<ChatRoomDTO> createRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long targetUserId) {
        return ResponseEntity.ok(chatService.createChatRoom(userDetails.getId(), targetUserId));
    }

    @GetMapping("/rooms")
    public ResponseEntity<Page<ChatRoomDTO>> getRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(chatService.getChatRooms(userDetails.getId(), pageable));
    }

    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageDTO>> getMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long roomId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(chatService.getChatMessages(roomId, userDetails.getId(), pageable));
    }

    @MessageMapping("/chat/message")
    public void message(ChatMessageDTO message) {
        ChatMessageDTO savedMessage = chatService.saveMessage(message);
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), savedMessage);
    }

    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long roomId) {
        chatService.markMessagesAsRead(roomId, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<Void> leaveRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long roomId) {
        chatService.leaveRoom(userDetails.getId(), roomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/room/{roomId}/block")
    public ResponseEntity<Void> blockUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long roomId) {
        chatService.blockUser(userDetails.getId(), roomId);
        return ResponseEntity.ok().build();
    }
}