package com.prgrmsfinal.skypedia.post.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.post.dto.PostCategoryRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostCategoryResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.PostCategory;

public interface PostCategoryService {
	PostCategoryResponseDTO.Read read(String name);

	List<PostCategoryResponseDTO.Read> readAll();

	void create(Authentication authentication, PostCategoryRequestDTO.Create request);

	Optional<PostCategory> getByName(String name);

	boolean existsByName(String name);
}
