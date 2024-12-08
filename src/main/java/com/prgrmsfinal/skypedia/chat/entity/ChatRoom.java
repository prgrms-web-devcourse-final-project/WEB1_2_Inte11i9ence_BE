package com.prgrmsfinal.skypedia.chat.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Member creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Member participant;

    @Enumerated(EnumType.STRING)
    private ChatRoomStatus status = ChatRoomStatus.ACTIVE;

    private boolean isCreatorLeft;
    private boolean isParticipantLeft;
    private boolean isCreatorBlocked;
    private boolean isParticipantBlocked;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public ChatRoom(Member creator, Member participant, ChatRoomStatus status) {
        this.creator = creator;
        this.status = status;
        this.participant = participant;
    }

    public void leaveRoom(Member user) {
        if (creator.equals(user) || participant.equals(user)) {
            // 한 명이라도 나가면 채팅방을 삭제 상태로 변경
            this.status = ChatRoomStatus.DELETED;
        } else {
            throw new IllegalArgumentException("User is not in this chat room");
        }
    }

    public void blockUser(Member user) {
        if (creator.equals(user)) {
            this.isCreatorBlocked = true;
        } else if (participant.equals(user)) {
            this.isParticipantBlocked = true;
        } else {
            throw new IllegalArgumentException("User is not in this chat room");
        }

        this.status = ChatRoomStatus.BLOCKED;
    }

    public boolean isMember(Member user) {
        return creator.equals(user) || participant.equals(user);
    }

    public boolean canSendMessage(Member user) {
        return isMember(user) && status == ChatRoomStatus.ACTIVE
                && !(isCreatorBlocked && creator.equals(user))
                && !(isParticipantBlocked && participant.equals(user));
    }
}