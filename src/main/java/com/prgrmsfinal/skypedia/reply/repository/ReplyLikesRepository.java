package com.prgrmsfinal.skypedia.reply.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.reply.entity.ReplyLikes;
import com.prgrmsfinal.skypedia.reply.entity.key.ReplyLikesId;

@Repository
public interface ReplyLikesRepository extends JpaRepository<ReplyLikes, ReplyLikesId> {
	boolean existsByReplyIdAndMemberId(Long replyId, Long memberId);

	@Query("SELECT rl FROM ReplyLikes rl WHERE rl.reply.id = :replyId AND rl.member.id = :memberId")
	Optional<ReplyLikes> findByReplyIdAndMemberId(@Param("replyId") Long replyId, @Param("memberId") Long memberId);

	@Query("SELECT rl.member.id FROM ReplyLikes rl WHERE rl.reply.id = :replyId")
	Set<Long> findMemberIdsByReplyId(@Param("replyId") Long replyId);

	@Query("DELETE FROM ReplyLikes rl WHERE rl.reply.id = :replyId AND rl.member.id = :memberId")
	void deleteByReplyIdAndReplyId(@Param("replyId") Long replyId, @Param("memberId") Long memberId);
}
