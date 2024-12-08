package com.prgrmsfinal.skypedia.photo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.prgrmsfinal.skypedia.photo.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

	@Query("SELECT p.s3FileKey FROM Photo p WHERE p.id = :id")
	String findS3FileKeyById(@Param("id") Long id);

	@Modifying
	@Transactional
	@Query("UPDATE Photo p SET p.contentType = :contentType, p.originalFileName = :originalFileName WHERE p.id = :id")
	int updatePhotoDetails(Long id, String contentType, String originalFileName);

	@Query("SELECT p.photo FROM PostPhoto p WHERE p.post.id = :postId")
	List<Photo> findPhotosByPostId(@Param("postId") Long postId);

	@Query("SELECT ph.s3FileKey FROM PostPhoto pp JOIN pp.photo ph WHERE pp.post.id = :postId")
	List<String> findS3FileKeysByPostId(@Param("postId") Long postId);

}
