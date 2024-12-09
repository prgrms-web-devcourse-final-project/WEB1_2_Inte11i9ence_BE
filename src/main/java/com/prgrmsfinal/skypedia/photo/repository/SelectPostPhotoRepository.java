package com.prgrmsfinal.skypedia.photo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrmsfinal.skypedia.photo.entity.SelectPostPhoto;
import com.prgrmsfinal.skypedia.photo.entity.SelectPostPhotoId;

import jakarta.transaction.Transactional;

public interface SelectPostPhotoRepository extends JpaRepository<SelectPostPhoto, SelectPostPhotoId> {

	@Modifying
	@Transactional
	@Query("DELETE FROM SelectPostPhoto spp WHERE spp.selectPost.id = :selectPostId")
	void deleteBySelectPostId(@Param("selectPostId") Long selectPostId);
}
