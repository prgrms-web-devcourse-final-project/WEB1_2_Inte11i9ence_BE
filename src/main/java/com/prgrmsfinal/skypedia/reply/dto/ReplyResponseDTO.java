package com.prgrmsfinal.skypedia.reply.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ReplyResponseDTO {
	@Getter
	@AllArgsConstructor
	@Builder
	public static class Read {
		private final Long id;

		private final Long parentId;

		private final MemberResponseDTO.Info author;

		private final String content;

		private final boolean liked;

		private final Long likes;

		private final boolean deleted;

		private final LocalDateTime repliedAt;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	public static class ReadAll {
		private final List<ReplyResponseDTO.Read> replies;

		private final String nextUri;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	public static class ToggleLikes {
		private final boolean liked;

		private final Long likes;
	}

}
