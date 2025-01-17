package com.prgrmsfinal.skypedia.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.post.entity.PostCategory;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
	boolean existsByName(@Param("name") String name);

	@Query("SELECT p FROM PostCategory p WHERE p.name = :name")
	Optional<PostCategory> findByName(@Param("name") String name);
}
