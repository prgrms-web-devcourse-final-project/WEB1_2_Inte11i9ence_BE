package com.prgrmsfinal.skypedia.chat.dto;

import com.prgrmsfinal.skypedia.chat.entity.ChatRoom;
import com.prgrmsfinal.skypedia.chat.entity.ChatRoomStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomDTO {
    private Long id;
    private Long creatorId;
    private Long participantId;
    private String creatorName;
    private String participantName;
    private ChatRoomStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ChatRoomDTO from(ChatRoom chatRoom) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .creatorId(chatRoom.getCreator().getId())
                .participantId(chatRoom.getParticipant().getId())
                .creatorName(chatRoom.getCreator().getUsername())
                .participantName(chatRoom.getParticipant().getUsername())
                .status(chatRoom.getStatus())
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
    }
}