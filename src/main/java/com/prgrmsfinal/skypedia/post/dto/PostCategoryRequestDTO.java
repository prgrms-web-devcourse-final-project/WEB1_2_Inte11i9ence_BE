package com.prgrmsfinal.skypedia.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PostCategoryRequestDTO {
	@Getter
	@AllArgsConstructor
	public static class Create {
		private final String name;

		private final String description;
	}
}
