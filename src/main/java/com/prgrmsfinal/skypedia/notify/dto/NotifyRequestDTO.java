package com.prgrmsfinal.skypedia.notify.dto;

import java.time.LocalDateTime;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.notify.constant.NotifyType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class NotifyRequestDTO {
	@Getter
	@AllArgsConstructor
	@Builder
	public static class User {
		private final Member member;

		private final String content;

		private final NotifyType notifyType;

		private final String uri;

		@Builder.Default
		private final LocalDateTime sentAt = LocalDateTime.now();
	}

	@Getter
	@AllArgsConstructor
	@Builder
	public static class Global {
		private final String content;

		@Builder.Default
		private final NotifyType notifyType = NotifyType.NOTICE;

		private final String uri;

		@Builder.Default
		private final LocalDateTime sentAt = LocalDateTime.now();
	}
}
