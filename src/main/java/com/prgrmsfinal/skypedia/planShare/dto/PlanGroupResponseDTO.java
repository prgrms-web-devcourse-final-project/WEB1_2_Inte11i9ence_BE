package com.prgrmsfinal.skypedia.planShare.dto;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PlanGroupResponseDTO {
	@Schema(title = "PlanGroup 목록 조회 응답 DTO", description = "일정 그룹 목록 조회 응답 DTO입니다.")
	@Getter
	@Builder
	@AllArgsConstructor
	public static class ReadAll {
		@Schema(title = "일정 공유 게시글 목록", description = "일정 공유 게시글의 목록입니다.")
		private final List<PlanGroupResponseDTO.Info> planShare;

		@Schema(title = "다음 게시글 목록 URL", description = "다음 게시글 목록 URL입니다.")
		private final String nextUri;

	}

	@Schema(title = "PlanGroup 단일 조회 응답 DTO", description = "단일 일정 그룹 조회 응답 DTO입니다.")
	@Getter
	@Builder
	@AllArgsConstructor
	public static class Read {

		@Schema(title = "일정 그룹 ID", description = "일정 그룹의 고유 ID입니다.", example = "1")
		private final Long id;

		@Schema(title = "작성자 정보", description = "닉네임, 프로필이미지 데이터입니다.")
		private final MemberResponseDTO.Info author;

		@Schema(title = "지역 ID", description = "일정 그룹이 속한 지역의 ID입니다.", example = "101")
		private final String regionName;

		@Schema(title = "일정 제목", description = "일정 그룹의 제목입니다.", example = "가족 여행 일정")
		private final String title;

		@Schema(title = "조회수", description = "일정 그룹의 조회수입니다.", example = "123")
		private final Long views;

		@Schema(title = "좋아요 수", description = "일정 그룹의 좋아요 수입니다.", example = "45")
		private final Long likes;

		@Schema(title = "좋아요 여부", description = "회원의 좋아요 토글 여부입니다.", example = "true")
		private final boolean liked;

		@Schema(title = "스크랩 여부", description = "회원의 스크랩 토글 여부입니다.", example = "true")
		private final boolean scraped;

		@Schema(title = "수정 일자", description = "일정 그룹의 마지막 수정 일자입니다.", example = "2024-12-03T14:00:00")
		private final LocalDateTime updatedAt;

		@Schema(title = "이미지 목록", description = "게시글의 이미지 데이터 목록입니다.")
		private final List<PhotoResponseDTO.Info> photos;

		@Schema(title = "세부 일정 목록", description = "일정 그룹에 포함된 세부 일정 목록입니다.")
		private final LinkedList<PlanDetailResponseDTO.Read> planDetails;

		@Schema(title = "댓글 정보", description = "게시글의 댓글 데이터 목록입니다.")
		private final ReplyResponseDTO.ReadAll reply;
	}

	@Getter
	@AllArgsConstructor
	public static class Search {
		private final Long id;

		private final Member member;

		private final String regionName;

		private final String title;

		private final String content;

		// private final LinkedList<PlanDetail> planDetails;

		private final Long views;

		private final Long likes;

		private final boolean deleted;

		private final LocalDateTime updatedAt;

		private final LocalDateTime deletedAt;

		private final double relevance;
	}

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
	public static class ToggleLikes {
		@Schema(title = "좋아요 여부", description = "회원의 좋아요 토글 여부입니다.", example = "true")
		private final boolean liked;

		@Schema(title = "좋아요수", description = "게시글의 좋아요수입니다.", example = "200")
		private final Long likes;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	public static class Info {
		@Schema(title = "게시글 ID", description = "게시글 ID입니다.")
		private final Long id;

		@Schema(title = "작성자 정보", description = "닉네임, 프로필이미지 데이터입니다.")
		private final MemberResponseDTO.Info author;

		@Schema(title = "지역 카테고리", description = "일정 그룹이 속한 지역 카테고리입니다.")
		private final String regionName;

		@Schema(title = "게시글 제목", description = "게시글 제목입니다.")
		private final String title;

		@Schema(title = "조회수", description = "게시글의 조회수입니다.")
		private final Long views;

		@Schema(title = "좋아요수", description = "게시글의 좋아요수입니다.")
		private final Long likes;

		@Schema(title = "댓글수", description = "댓글의 개수입니다.")
		private final Long replies;

		@Schema(title = "일정 그룹 썸네일 URL", description = "일정 공유 게시물의 썸네일 URL입니다.")
		private final String thumbnailUrl;

		@Schema(title = "수정 일자", description = "일정 그룹의 마지막 수정 일자입니다.")
		private LocalDateTime updatedAt;
	}
}
