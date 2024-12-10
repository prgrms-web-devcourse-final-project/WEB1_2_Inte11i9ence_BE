package com.prgrmsfinal.skypedia.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	@Query("SELECT p FROM Post p WHERE p.id = :postId AND p.deleted = :deleted")
	Optional<Post> findByIdAndDeleted(@Param("postId") Long id, @Param("deleted") boolean deleted);

	@Query("SELECT p FROM Post p WHERE p.deleted = :deleted")
	Slice<Post> findAllByDeleted(@Param("deleted") boolean deleted, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.deleted = :deleted AND p.category.name = :category")
	Slice<Post> findAllByCategory(@Param("deleted") boolean deleted, @Param("category") String category, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.deleted = :deleted AND p.member.username = :username")
	Slice<Post> findAllByUsername(@Param("username") String username, @Param("deleted") boolean deleted, Pageable pageable);

	@Query(value = "SELECT p.*, MATCH(p.title) AGAINST (:keyword IN BOOLEAN MODE) AS relevance "
		+ "FROM post p "
		+ "WHERE MATCH(p.title) AGAINST (:keyword IN BOOLEAN MODE) "
		+ "ORDER BY relevance DESC, p.title ASC "
		+ "LIMIT 10 OFFSET :offset", nativeQuery = true)
	List<Post> findPostsByTitleKeyword(@Param("keyword") String keyword, @Param("offset") int offset);

	@Query(value = "SELECT p.*, MATCH(p.hashtags) AGAINST (:keyword IN BOOLEAN MODE) AS relevance "
		+ "FROM post p "
		+ "WHERE MATCH(p.hashtags) AGAINST (:keyword IN BOOLEAN MODE) "
		+ "ORDER BY relevance DESC, p.title ASC "
		+ "LIMIT 10 OFFSET :offset", nativeQuery = true)
	List<Post> findPostsByHashtagsKeyword(@Param("keyword") String keyword, @Param("offset") int offset);

	@Modifying
	@Transactional
	@Query("UPDATE Post p SET p.views = p.views + :views WHERE p.id = :id")
	int updateViewsCount(@Param("id") Long id, @Param("views") Long views);

	@Modifying
	@Transactional
	@Query("UPDATE Post p SET p.likes = p.likes + :likes WHERE p.id = :id")
	void updateLikesCount(@Param("id") Long id, @Param("likes") Long likes);

	@Query("SELECT p.likes FROM Post p WHERE p.id = :id")
	Long findLikesById(Long id);

	@Query("SELECT p.views FROM Post p WHERE p.id = :id")
	Long findViewsById(Long id);
}
