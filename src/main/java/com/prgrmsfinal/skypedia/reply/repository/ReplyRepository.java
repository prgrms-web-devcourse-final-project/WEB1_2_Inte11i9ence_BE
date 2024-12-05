package com.prgrmsfinal.skypedia.reply.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.reply.entity.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
	@Query("SELECT r FROM Reply r WHERE r.id = :replyId AND r.deleted = :deleted")
	Optional<Reply> findByIdAndDeleted(@Param("replyId") Long replyId, @Param("deleted") boolean deleted);

	@Query("SELECT r FROM Reply r WHERE r.parentReply.id = :parentId AND r.id > :lastReplyId ORDER BY r.id ASC")
	List<Reply> findRepliesByParentId(@Param("parentId") Long parentId, @Param("lastReplyId") Long lastReplyId,
		Pageable pageable);
}
