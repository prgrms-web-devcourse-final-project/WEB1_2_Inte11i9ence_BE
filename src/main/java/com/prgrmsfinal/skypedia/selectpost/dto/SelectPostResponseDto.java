package com.prgrmsfinal.skypedia.selectpost.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SelectPostResponseDto {
	private Long selectPostId;
	private String content;
	private List<String> presignedUrls;

	// 전체 조회를 위한 추가 필드들
	private LocalDateTime createdAt;
	private MemberResponseDTO.Info author;

	@Getter
	@Builder
	public static class ListResponse {
		private List<SelectPostResponseDto> selectPosts;
		private boolean hasNext;
		private Long lastId;
	}

	public static class OneInfoResponse {
		private Long selectPostId;

	}
}