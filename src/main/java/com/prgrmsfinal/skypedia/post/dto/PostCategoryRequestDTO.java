package com.prgrmsfinal.skypedia.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(title = "게시글 카테고리 요청 DTO", description = "게시글 카테고리 요청에 사용하는 DTO입니다.")
public class PostCategoryRequestDTO {
	@Getter
	@AllArgsConstructor
	@Schema(title = "게시글 카테고리 생성 요청 DTO", description = "게시글 카테고리 생성 요청에 사용하는 DTO입니다.")
	public static class Create {
		@Schema(title = "이름", description = "게시글 카테고리의 이름입니다.", example = "서울")
		private final String name;

		@Schema(title = "설명", description = "게시글 카테고리의 설명입니다.", example = "서울은 아름다운 도시입니다.")
		private final String description;
	}
}
