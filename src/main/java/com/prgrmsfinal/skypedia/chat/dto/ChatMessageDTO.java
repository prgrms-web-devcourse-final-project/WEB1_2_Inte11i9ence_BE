package com.prgrmsfinal.skypedia.chat.dto;

import com.prgrmsfinal.skypedia.chat.entity.ChatMessage;
import com.prgrmsfinal.skypedia.chat.entity.MessageStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageDTO {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private MessageStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public static ChatMessageDTO from(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getUsername())
                .content(message.getContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .readAt(message.getReadAt())
                .build();
    }
}