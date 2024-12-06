package com.prgrmsfinal.skypedia.notify.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class NotifyResponseDTO {
	@Getter
	@AllArgsConstructor
	@Builder
	public static class Send {
		private final String content;

		private final String type;

		private final String uri;

		private final LocalDateTime sentAt;

		private final boolean read;
	}
}
