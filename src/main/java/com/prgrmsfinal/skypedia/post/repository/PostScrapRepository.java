package com.prgrmsfinal.skypedia.post.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.post.entity.PostScrap;
import com.prgrmsfinal.skypedia.post.entity.key.PostScrapId;

@Repository
public interface PostScrapRepository extends JpaRepository<PostScrap, PostScrapId> {
	boolean existsByPostIdAndMemberId(Long postId, Long memberId);

	@Query("SELECT ps FROM PostScrap ps WHERE ps.post.id = :postId AND ps.member.id = :memberId")
	Optional<PostScrap> findByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

	@Query("SELECT ps.post FROM PostScrap ps WHERE ps.post.deleted = :deleted AND ps.member.id = :memberId")
	Slice<Post> findAllByScraped(@Param("memberId") Long memberId, @Param("deleted") boolean deleted, Pageable pageable);
}
