package com.prgrmsfinal.skypedia.selectpost.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrmsfinal.skypedia.selectpost.entity.SelectPost;

public interface SelectPostRepository extends JpaRepository<SelectPost, Long> {

	//무한스크롤
	@Query("SELECT sp FROM SelectPost sp " +
		"WHERE sp.createdAt < :currentTime " +
		"ORDER BY sp.createdAt DESC")
	List<SelectPost> findAllByOrderByCreatedAtDesc(
		@Param("currentTime") LocalDateTime currentTime,
		Pageable pageable
	);
}
