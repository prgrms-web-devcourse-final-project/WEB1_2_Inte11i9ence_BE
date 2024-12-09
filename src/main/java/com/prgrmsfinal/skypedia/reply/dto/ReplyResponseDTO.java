package com.prgrmsfinal.skypedia.reply.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(title = "댓글 응답 DTO", description = "댓글 응답에 사용하는 DTO 입니다.")
public class ReplyResponseDTO {
	@Getter
	@AllArgsConstructor
	@Builder
	@Schema(title = "댓글 단일 조회 응답 DTO", description = "댓글 단일 조회 응답에 사용하는 DTO 입니다.")
	public static class Read {
		@Schema(title = "댓글 ID", description = "조회할 댓글의 ID입니다.", example = "1")
		private final Long id;

		@Schema(title = "부모댓글 ID", description = "부모댓글 ID입니다.", example = "1")
		private final Long parentId;

		@Schema(title = "작성자 정보", description = "작성자의 정보입니다.")
		private final MemberResponseDTO.Info author;

		@Schema(title = "댓글 내용", description = "조회할 댓글 내용입니다.", example = "안녕하세요!!!")
		private final String content;

		@Schema(title = "좋아요 여부", description = "댓글에 좋아요를 눌렀는지 여부입니다.", example = "true")
		private final boolean liked;

		@Schema(title = "좋아요 수", description = "댓글의 좋아요 수입니다.", example = "5")
		private final Long likes;

		@Schema(title = "삭제 여부", description = "삭제된 댓글인지 여부입니다.", example = "false")
		private final boolean deleted;

		@Schema(title = "등록 일시", description = "댓글의 등록일시입니다.", example = "false")
		private final LocalDateTime repliedAt;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	@Schema(title = "댓글 목록 조회 응답 DTO", description = "댓글 목록 조회 응답에 사용하는 DTO 입니다.")
	public static class ReadAll {
		@Schema(title = "댓글 내용 리스트", description = "댓글의 내용을 담고있는 리스트입니다.")
		private final List<ReplyResponseDTO.Read> replies;

		@Schema(title = "다음 댓글 조회 URI", description = "다음 댓글 목록 조회에 사용할 URI입니다.")
		private final String nextUri;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	@Schema(title = "댓글 좋아요 응답 DTO", description = "댓글 좋아요 토글 응답에 사용하는 DTO 입니다.")
	public static class LikeStatus {
		@Schema(title = "좋아요 여부", description = "댓글에 좋아요를 눌렀는지 여부입니다.", example = "true")
		private final boolean liked;

		@Schema(title = "좋아요 수", description = "댓글의 좋아요 수입니다.", example = "5")
		private final Long likes;
	}
}
