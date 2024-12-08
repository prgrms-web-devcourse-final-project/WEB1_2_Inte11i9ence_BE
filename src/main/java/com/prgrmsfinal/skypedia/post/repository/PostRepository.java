package com.prgrmsfinal.skypedia.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	@Query("SELECT p FROM Post p WHERE p.id = :postId AND p.deleted = :deleted")
	Optional<Post> findByIdAndDeleted(@Param("postId") Long id, @Param("deleted") boolean deleted);

	@Query("SELECT p FROM Post p WHERE p.id < :lastPostId AND p.deleted = :deleted AND p.category.name = :category ORDER BY p.id")
	List<Post> findPostsById(@Param("lastPostId") Long lastPostId, @Param("deleted") boolean deleted,
		@Param("category") String category, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.id < :lastPostId AND p.deleted = :deleted AND p.member.username = :username ORDER BY p.id")
	List<Post> findPostsByUsername(@Param("username") String username, @Param("lastPostId") Long lastPostId,
		@Param("deleted") boolean deleted, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE (p.likes < :cursor OR (p.likes = :cursor AND p.id < :lastPostId)) AND p.deleted = :deleted AND p.category.name = :category ORDER BY p.likes DESC, p.id DESC")
	List<Post> findPostsByLikes(@Param("cursor") Long cursor, @Param("lastPostId") Long lastPostId,
		@Param("deleted") boolean deleted, @Param("category") String category, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.title LIKE CONCAT('%', :cursor, '%') AND p.id < :lastPostId AND p.deleted = :deleted AND p.category.name = :category ORDER BY p.title ASC, p.id DESC")
	List<Post> findPostsByTitle(@Param("cursor") String cursor, @Param("lastPostId") Long lastPostId,
		@Param("deleted") boolean deleted, @Param("category") String category, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.rating IS NOT NULL AND (p.rating < :cursor OR (p.rating = :cursor AND p.id < :lastPostId)) AND p.deleted = :deleted AND p.category.name = :category ORDER BY p.rating DESC, p.id DESC")
	List<Post> findPostsByRating(@Param("cursor") Float cursor, @Param("lastPostId") Long lastPostId,
		@Param("deleted") boolean deleted, @Param("category") String category, Pageable pageable);

	@Query(value = "SELECT *, MATCH(title) AGAINST (:keyword) AS relevance FROM post WHERE MATCH(title) AGAINST (:keyword) AND (MATCH(title) AGAINST (:keyword) < :lastRelevance OR (MATCH(title) AGAINST (:keyword) = :lastRelevance AND id < :lastPostId)) ORDER BY relevance DESC, id DESC LIMIT 10", nativeQuery = true)
	List<PostResponseDTO.Search> findPostsByTitleKeyword(@Param("keyword") String keyword,
		@Param("lastRelevance") double lastRelevance,
		@Param("lastPostId") Long lastPostId);

	@Query(value = "SELECT *, MATCH(hashtags) AGAINST (:keyword) AS relevance FROM post WHERE MATCH(hashtags) AGAINST (:keyword) AND (MATCH(hashtags) AGAINST (:keyword) < :lastRelevance OR (MATCH(hashtags) AGAINST (:keyword) = :lastRelevance = :lastRelevance AND id < :lastPostId)) ORDER BY relevance DESC, id DESC LIMIT 10", nativeQuery = true)
	List<PostResponseDTO.Search> findPostsByHashtagsKeyword(@Param("keyword") String keyword,
		@Param("lastRelevance") double lastRelevance,
		@Param("lastPostId") Long lastPostId);

	@Modifying
	@Query("UPDATE Post p SET p.views = p.views + :views WHERE p.id = :id")
	int incrementViewsById(@Param("id") Long id, @Param("views") Long views);

	@Modifying
	@Query("UPDATE Post p SET p.likes = p.likes + :likes WHERE p.id = :id")
	void incrementOrDecrementLikesById(@Param("id") Long id, @Param("likes") Long likes);
}
