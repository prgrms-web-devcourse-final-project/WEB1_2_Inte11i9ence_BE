package com.prgrmsfinal.skypedia.photo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PhotoResponseDTO {
	@Schema(title = "사진 정보 조회 DTO", description = "사진 정보 조회에 사용하는 DTO입니다.")
	@Getter
	@Builder
	@AllArgsConstructor
	public static class Info {
		@Schema(title = "사진 ID", description = "사진 ID입니다.", minimum = "1", example = "25")
		private final Long id;

		@Schema(title = "사진 URL", description = "사진 URL입니다.")
		private final String photoUrl;
	}
}
