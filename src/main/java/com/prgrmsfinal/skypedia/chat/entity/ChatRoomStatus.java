package com.prgrmsfinal.skypedia.chat.entity;

import lombok.Getter;

@Getter
public enum ChatRoomStatus {
    ACTIVE("활성"),
    BLOCKED("차단됨"),
    DELETED("삭제됨");

    private final String description;

    ChatRoomStatus(String description) {
        this.description = description;
    }
}