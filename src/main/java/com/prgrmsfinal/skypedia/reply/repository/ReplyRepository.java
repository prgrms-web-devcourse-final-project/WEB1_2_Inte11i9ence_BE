package com.prgrmsfinal.skypedia.reply.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.prgrmsfinal.skypedia.reply.entity.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
	@Query("SELECT r FROM Reply r WHERE r.id = :replyId AND r.deleted = :deleted")
	Optional<Reply> findByIdAndDeleted(@Param("replyId") Long replyId, @Param("deleted") boolean deleted);

	@Query("SELECT r FROM Reply r WHERE r.parentReply.id = :parentId")
	Slice<Reply> findAllByParentId(@Param("parentId") Long parentId, Pageable pageable);

	@Modifying
	@Transactional
	@Query("UPDATE Reply r SET r.likes = r.likes + :likes WHERE r.id = :id")
	void updateLikesCount(@Param("id") Long id, @Param("likes") Long likes);

	@Query("SELECT r.likes FROM Reply r WHERE r.id = :id")
	Long findLikesById(Long id);
}
