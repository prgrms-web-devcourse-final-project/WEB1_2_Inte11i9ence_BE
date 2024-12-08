package com.prgrmsfinal.skypedia.post.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.post.entity.PostReply;
import com.prgrmsfinal.skypedia.post.entity.key.PostReplyId;
import com.prgrmsfinal.skypedia.reply.entity.Reply;

@Repository
public interface PostReplyRepository extends JpaRepository<PostReply, PostReplyId> {
	@Query("SELECT pr.reply FROM PostReply pr WHERE pr.post.id = :postId AND pr.reply.id < :lastReplyId ORDER BY pr.repliedAt DESC")
	List<Reply> findRepliesByPostId(@Param("postId") Long postId, @Param("lastReplyId") Long lastReplyId,
		Pageable pageable);

	@Query("SELECT COUNT(pr) FROM PostReply pr WHERE pr.post.id = :postId")
	Long countRepliesByPostId(@Param("postId") Long postId);
}
