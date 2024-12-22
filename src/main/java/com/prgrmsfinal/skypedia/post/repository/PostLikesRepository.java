package com.prgrmsfinal.skypedia.post.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.post.entity.PostLikes;
import com.prgrmsfinal.skypedia.post.entity.key.PostLikesId;

import jakarta.transaction.Transactional;

@Repository
public interface PostLikesRepository extends JpaRepository<PostLikes, PostLikesId> {
	boolean existsByPostIdAndMemberId(Long postId, Long memberId);

	@Query("SELECT pl FROM PostLikes pl WHERE pl.post.id = :postId AND pl.member.id = :memberId")
	Optional<PostLikes> findByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

	@Query("SELECT pl.member.id FROM PostLikes pl WHERE pl.post.id = :postId")
	Set<Long> findMemberIdsByPostId(@Param("postId") Long postId);

	@Modifying
	@Transactional
	@Query("DELETE FROM PostLikes pl WHERE pl.post.id = :postId AND pl.member.id = :memberId")
	void deleteByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);
}
