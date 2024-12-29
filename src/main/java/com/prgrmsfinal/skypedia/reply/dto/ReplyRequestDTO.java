package com.prgrmsfinal.skypedia.reply.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prgrmsfinal.skypedia.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(title = "댓글 요청 DTO", description = "댓글 요청에 사용하는 DTO입니다.")
public class ReplyRequestDTO {
	@Getter
	@AllArgsConstructor
	@Builder
	@Schema(title = "댓글 생성 요청 DTO", description = "댓글 생성 요청에 사용하는 DTO입니다.")
	public static class Create {
		@Schema(title = "부모댓글 ID", description = "대댓글 등록에 사용할 부모댓글 ID입니다.", example = "1")
		private final Long parentId;

		@Schema(title = "댓글 내용", description = "작성할 댓글 내용입니다.", example = "안녕하세요!!!")
		private final String content;

		@Schema(title = "회원 객체", description = "작성할 댓글의 회원의 객체입니다.")
		private final Member member;
	}

	@Getter
	@Schema(title = "댓글 수정 요청 DTO", description = "댓글 수정 요청에 사용하는 DTO입니다.")
	public static class Modify {
		@Schema(title = "댓글 내용", description = "수정할 댓글 내용입니다.", example = "반가워요!!!")
		@NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
		private final String content;

		@JsonCreator
		public Modify(@JsonProperty("content") String content) {
			this.content = content;
		}
	}
}
