package com.prgrmsfinal.skypedia.post.util;

import com.prgrmsfinal.skypedia.post.dto.PostCategoryRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostCategoryResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.PostCategory;

public class PostCategoryMapper {
	public static PostCategoryResponseDTO.Read toDTO(PostCategory postCategory) {
		return new PostCategoryResponseDTO.Read(postCategory.getName(), postCategory.getDescription());
	}

	public static PostCategory toEntity(PostCategoryRequestDTO.Create dto) {
		return PostCategory.builder()
			.name(dto.getName())
			.description(dto.getDescription())
			.build();
	}
}
