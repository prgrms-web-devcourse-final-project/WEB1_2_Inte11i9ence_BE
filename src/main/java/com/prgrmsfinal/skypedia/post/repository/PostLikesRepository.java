package com.prgrmsfinal.skypedia.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.post.entity.PostLikes;
import com.prgrmsfinal.skypedia.post.entity.key.PostLikesId;

@Repository
public interface PostLikesRepository extends JpaRepository<PostLikes, PostLikesId> {
	@Query(value = "SELECT EXISTS (SELECT 1 FROM post_likes WHERE post_id = :postId AND member_id = :memberId)", nativeQuery = true)
	boolean existsByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

	@Query("SELECT pl FROM PostLikes pl WHERE pl.post.id = :postId AND pl.member.id = :memberId")
	Optional<PostLikes> findByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);
}
