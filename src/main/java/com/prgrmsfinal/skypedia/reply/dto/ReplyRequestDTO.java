package com.prgrmsfinal.skypedia.reply.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ReplyRequestDTO {
	@Getter
	@AllArgsConstructor
	@Builder
	public static class Modify {
		@NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
		private final String content;
	}
}
