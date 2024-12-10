package com.prgrmsfinal.skypedia.chat.repository;

import com.prgrmsfinal.skypedia.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE cm.chatRoom.id = :roomId " +
            "ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findByChatRoomId(@Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
            "WHERE cm.chatRoom.id = :roomId " +
            "AND cm.sender.id != :userId " +
            "AND cm.status = 'SENT'")
    long countUnreadMessages(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE cm.chatRoom.id = :roomId " +
            "ORDER BY cm.createdAt DESC LIMIT 1")
    Optional<ChatMessage> findLatestMessage(@Param("roomId") Long roomId);

    void deleteAllByChatRoomId(Long chatRoomId);


}