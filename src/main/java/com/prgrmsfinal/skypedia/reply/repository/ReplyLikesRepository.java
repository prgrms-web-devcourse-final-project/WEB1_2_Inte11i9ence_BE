package com.prgrmsfinal.skypedia.reply.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.reply.entity.ReplyLikes;
import com.prgrmsfinal.skypedia.reply.entity.key.ReplyLikesId;

@Repository
public interface ReplyLikesRepository extends JpaRepository<ReplyLikes, ReplyLikesId> {
	@Query(value = "SELECT EXISTS (SELECT 1 FROM reply_likes WHERE reply_id = :replyId AND member_id = :memberId)", nativeQuery = true)
	boolean existsByReplyIdAndMemberId(@Param("replyId") Long replyId, @Param("memberId") Long memberId);

	@Query("SELECT rl FROM ReplyLikes rl WHERE rl.reply.id = :replyId AND rl.member.id = :memberId")
	Optional<ReplyLikes> findByReplyIdAndMemberId(@Param("replyId") Long replyId, @Param("memberId") Long memberId);
}
