package com.prgrmsfinal.skypedia.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PostCategoryResponseDTO {
	@Getter
	@AllArgsConstructor
	public static class Read {
		private final String name;

		private final String description;
	}
}
