package com.prgrmsfinal.skypedia.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.post.entity.PostScrap;
import com.prgrmsfinal.skypedia.post.entity.key.PostScrapId;

@Repository
public interface PostScrapRepository extends JpaRepository<PostScrap, PostScrapId> {
	@Query(value = "SELECT EXISTS (SELECT 1 FROM post_scrap WHERE post_id = :postId AND member_id = :memberId)", nativeQuery = true)
	boolean existsByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);
}
