package com.prgrmsfinal.skypedia.chat.repository;

import com.prgrmsfinal.skypedia.chat.entity.ChatMessage;
import com.prgrmsfinal.skypedia.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE (cr.creator.id = :userId OR cr.participant.id = :userId) " +
            "AND cr.status = 'ACTIVE' " +
            "ORDER BY cr.updatedAt DESC")
    Page<ChatRoom> findActiveChatRooms(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE cr.creator.id = :creatorId AND cr.participant.id = :participantId " +
            "OR cr.creator.id = :participantId AND cr.participant.id = :creatorId")
    Optional<ChatRoom> findByUsers(@Param("creatorId") Long creatorId,
                                   @Param("participantId") Long participantId);

    @Query("SELECT COUNT(cr) FROM ChatRoom cr " +
            "WHERE (cr.creator.id = :userId OR cr.participant.id = :userId) " +
            "AND cr.status = 'ACTIVE'")
    long countActiveRooms(@Param("userId") Long userId);

    Optional<ChatRoom> findByCreatorIdAndParticipantIdOrParticipantIdAndCreatorId(Long creatorId, Long participantId, Long creatorId1, Long participantId1);
}