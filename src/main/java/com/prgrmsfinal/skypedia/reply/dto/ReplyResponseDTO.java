package com.prgrmsfinal.skypedia.reply.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ReplyResponseDTO {
	@Getter
	@AllArgsConstructor
	public static class ReadAll {
		List<ReplyResponseDTO.Info> replies;

		private final String nextReplyUrl;
	}

	@Getter
	@AllArgsConstructor
	public static class Info {
		private Long id;

		private Long parentId;

		private MemberResponseDTO.Info author;

		private final String content;

		private final Long likes;

		private final LocalDateTime repliedAt;
	}
}
