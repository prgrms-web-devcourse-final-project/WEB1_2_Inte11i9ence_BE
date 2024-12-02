package com.prgrmsfinal.skypedia.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	@Query("SELECT p FROM Post p WHERE p.id = :postId AND p.deleted = :deleted")
	Optional<Post> findByIdAndDeleted(@Param("postId") Long id, @Param("deleted") boolean deleted);

	// ※ 목록 조회는 QueryDsl을 적용하여 뜯어고칠 예정!!!

	@Query("SELECT p FROM Post p WHERE p.id > :lastPostId AND p.deleted = :deleted AND p.category.name = :category ORDER BY p.id DESC LIMIT 20")
	List<Post> findRecentPostsAfterId(@Param("lastPostId") Long lastPostId, @Param("deleted") boolean deleted,
		@Param("category") String category);

	@Query("SELECT p FROM Post p WHERE p.id > :lastPostId AND p.deleted = :deleted AND p.category.name = :category ORDER BY p.views DESC, p.id DESC LIMIT 20")
	List<Post> findPostsByViews(@Param("lastPostId") Long lastPostId, @Param("deleted") boolean deleted,
		@Param("category") String category);

	@Query("SELECT p FROM Post p WHERE p.id > :lastPostId AND p.deleted = :deleted AND p.category.name = :category ORDER BY p.likes DESC, p.id DESC LIMIT 20")
	List<Post> findPostsByLikes(@Param("lastPostId") Long lastPostId, @Param("deleted") boolean deleted,
		@Param("category") String category);

	@Query("SELECT p FROM Post p WHERE p.id > :lastPostId AND p.deleted = :deleted AND p.category.name = :category ORDER BY p.title ASC, p.id DESC LIMIT 20")
	List<Post> findPostsByTitle(@Param("lastPostId") Long lastPostId, @Param("deleted") boolean deleted,
		@Param("category") String category);

	@Query("SELECT p FROM Post p WHERE p.id > :lastPostId AND p.deleted = :deleted AND p.category.name = :category ORDER BY p.rating DESC, p.id DESC LIMIT 20")
	List<Post> findPostsByRating(@Param("lastPostId") Long lastPostId, @Param("deleted") boolean deleted,
		@Param("category") String category);

	@Modifying
	@Query("UPDATE Post p SET p.views = p.views + :views WHERE p.id = :id")
	int incrementViewsById(@Param("id") Long id, @Param("views") Long views);

	@Modifying
	@Query("UPDATE Post p SET p.likes = p.likes + :likes WHERE p.id = :id")
	void incrementOrDecrementLikesById(@Param("id") Long id, @Param("likes") Long likes);
}
