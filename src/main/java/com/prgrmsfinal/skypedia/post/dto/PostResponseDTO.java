package com.prgrmsfinal.skypedia.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.PostCategory;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PostResponseDTO {
	@Schema(title = "게시글 단일 조회 응답 DTO", description = "게시글의 단일 조회 응답에 사용하는 DTO입니다.")
	@Getter
	@AllArgsConstructor
	@Builder
	public static class Read {
		@Schema(title = "게시글 ID", description = "게시글 ID입니다.", minimum = "1", example = "25")
		private final Long id;

		@Schema(title = "게시글 제목", description = "게시글 제목입니다.", example = "게시글 제목")
		private final String title;

		@Schema(title = "게시글 내용", description = "게시글 내용입니다.", example = "게시글 내용")
		private final String content;

		@Schema(title = "작성자 정보", description = "닉네임, 프로필이미지 데이터입니다.")
		private final MemberResponseDTO.Info author;

		@Schema(title = "조회수", description = "게시글의 조회수입니다.", example = "120")
		private final Long views;

		@Schema(title = "좋아요수", description = "게시글의 좋아요수입니다.", example = "200")
		private final Long likes;

		@Schema(title = "별점", description = "0.0 ~ 5.0점 사이의 별점입니다.", example = "5.0")
		private final Float rating;

		@Schema(title = "좋아요 여부", description = "회원의 좋아요 토글 여부입니다.", example = "true")
		private final boolean liked;

		@Schema(title = "스크랩 여부", description = "회원의 스크랩 토글 여부입니다.", example = "true")
		private final boolean scraped;

		@Schema(title = "게시글 카테고리", description = "게시글 카테고리입니다.", example = "자유")
		private final String category;

		@Schema(title = "게시글 수정일시", description = "게시글이 수정된 일시입니다.", example = "2024-11-19T10:15:30")
		private final LocalDateTime postedAt;

		@Schema(title = "해쉬태그", description = "해쉬태그 목록입니다.")
		private final List<String> hashtags;

		@Schema(title = "이미지 목록", description = "게시글의 이미지 데이터 목록입니다.")
		private final List<PhotoResponseDTO.Info> photos;

		@Schema(title = "댓글 정보", description = "게시글의 댓글 데이터 목록입니다.")
		private final ReplyResponseDTO.ReadAll reply;
	}

	@Schema(title = "게시글 목록 조회 응답 DTO", description = "게시글의 목록 조회 응답에 사용하는 DTO입니다.")
	@Getter
	@AllArgsConstructor
	public static class ReadAll {
		@Schema(title = "게시글 목록", description = "게시글의 목록입니다.")
		private final List<PostResponseDTO.Info> posts;

		@Schema(title = "다음 게시글 목록 URL", description = "다음 게시글 목록 URL입니다.")
		private final String nextUri;
	}

	@Schema(title = "게시글 통계 응답 DTO", description = "게시글의 통계 조회 응답에 사용하는 DTO입니다.")
	@Getter
	@AllArgsConstructor
	@Builder
	public static class Statistics {
		@Schema(title = "조회수", description = "게시글의 조회수입니다.", example = "120")
		private final Long views;

		@Schema(title = "좋아요수", description = "게시글의 좋아요수입니다.", example = "200")
		private final Long likes;

		@Schema(title = "좋아요 여부", description = "회원의 좋아요 토글 여부입니다.", example = "true")
		private final boolean liked;

		@Schema(title = "스크랩 여부", description = "회원의 스크랩 토글 여부입니다.", example = "true")
		private final boolean scraped;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	@Schema(title = "게시글 좋아요 DTO", description = "게시글 좋아요 토글 응답에 사용하는 DTO입니다.")
	public static class LikeStatus {
		@Schema(title = "좋아요 여부", description = "회원의 좋아요 토글 여부입니다.", example = "true")
		private final boolean liked;

		@Schema(title = "좋아요수", description = "게시글의 좋아요수입니다.", example = "200")
		private final Long likes;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	@Schema(title = "게시글 내용 응답 DTO", description = "게시글 내용 응답에 사용하는 DTO입니다.")
	public static class Info {
		@Schema(title = "게시글 ID", description = "게시글 ID입니다.", minimum = "1", example = "25")
		private final Long id;

		@Schema(title = "게시글 제목", description = "게시글 제목입니다.", example = "게시글 제목")
		private final String title;

		@Schema(title = "게시글 내용", description = "게시글 내용입니다.", example = "게시글 내용")
		private final String content;

		@Schema(title = "작성자 정보", description = "닉네임, 프로필이미지 데이터입니다.")
		private final MemberResponseDTO.Info author;

		@Schema(title = "조회수", description = "게시글의 조회수입니다.", example = "120")
		private final Long views;

		@Schema(title = "좋아요수", description = "게시글의 좋아요수입니다.", example = "200")
		private final Long likes;

		@Schema(title = "댓글수", description = "댓글의 개수입니다.", example = "5")
		private final Long replies;

		@Schema(title = "게시글 카테고리", description = "게시글 카테고리입니다.", example = "자유")
		private final String category;

		@Schema(title = "별점", description = "0.0 ~ 5.0점 사이의 별점입니다.", example = "5.0")
		private final Float rating;

		@Schema(title = "게시글 작성일시", description = "게시글이 작성된 일시입니다.", example = "2024-11-19T10:15:30")
		private final LocalDateTime postedAt;

		@Schema(title = "대표 이미지 URL", description = "게시글 대표 이미지 URL입니다.")
		private final String photoUrl;
	}
}
