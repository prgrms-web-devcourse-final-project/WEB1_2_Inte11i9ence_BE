package com.prgrmsfinal.skypedia.photo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class PhotoRequestDTO {
	@Schema(title = "사진 업로드 요청 DTO", description = "사진 업로드 요청에 사용하는 DTO입니다.")
	@Getter
	@AllArgsConstructor
	public static class Upload {
		@Schema(title = "원본 파일명", description = "사진 파일의 원본 파일명입니다.", example = "photo1.jpg")
		@Pattern(regexp = "^[\\p{L}\\p{N}_\\-. ]+(\\.jpg|\\.jpeg|\\.png|\\.gif|\\.bmp|\\.webp|\\.svg)$"
			, message = "파일명 또는 확장자가 올바르지 않습니다.")
		private final String originalFileName;

		@Schema(title = "파일 타입", description = "사진 파일의 파일 타입입니다.", example = "image/jpeg")
		@Pattern(regexp = "^image\\/(jpg|jpeg|png|gif|bmp|webp|svg|)$", message = "파일 타입이 올바르지 않습니다.")
		private final String contentType;

	}
}
