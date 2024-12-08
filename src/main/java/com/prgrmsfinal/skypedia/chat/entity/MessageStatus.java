package com.prgrmsfinal.skypedia.chat.entity;

import lombok.Getter;

@Getter
public enum MessageStatus {
    SENT("전송됨"),
    READ("읽음"),
    DELETED("삭제됨");

    private final String description;

    MessageStatus(String description) {
        this.description = description;
    }
}