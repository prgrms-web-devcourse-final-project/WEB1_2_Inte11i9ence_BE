package com.prgrmsfinal.skypedia.photo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.photo.entity.PostPhoto;
import com.prgrmsfinal.skypedia.photo.entity.PostPhotoId;

import jakarta.transaction.Transactional;

@Repository
public interface PostPhotoRepository extends JpaRepository<PostPhoto, PostPhotoId> {
	@Modifying
	@Transactional
	@Query("DELETE FROM PostPhoto pp WHERE pp.post.id = :postId")
	void deleteByPostId(@Param("postId") Long postId);

	@Modifying
	@Transactional
	@Query("DELETE FROM PostPhoto pp WHERE pp.post.id = :postId AND pp.photo.id IN :photoIds")
	void deleteByPostIdAndPhotoIdIn(@Param("postId") Long postId, @Param("photoIds") List<Long> photoIds);

	// 게시글 ID로 해당 게시글의 모든 사진 연결 조회
	@Query("SELECT pp FROM PostPhoto pp JOIN FETCH pp.photo WHERE pp.post.id = :postId")
	List<PostPhoto> findByPostId(@Param("postId") Long postId);

	//post id 검출중 젤 높은거 뽑는거임
	@Query("SELECT pp FROM PostPhoto pp WHERE pp.post.id = :postId ORDER BY pp.photo.id ASC LIMIT 1")
	Optional<PostPhoto> findTopByPostIdOrderByPhotoIdAsc(@Param("postId") Long postId);
}