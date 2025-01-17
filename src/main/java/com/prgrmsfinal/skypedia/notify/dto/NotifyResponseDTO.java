package com.prgrmsfinal.skypedia.notify.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(title = "알림 응답 DTO", description = "알림 응답에 사용하는 DTO 입니다.")
public class NotifyResponseDTO {
	@Getter
	@Schema(title = "알림 전송 응답 DTO", description = "알림 전송 응답에 사용하는 DTO 입니다.")
	public static class Send {
		@Schema(title = "알림 ID", description = "알림 ID입니다.", example = "1")
		private final Long id;

		@Schema(title = "알림 내용", description = "알림 내용입니다.", example = "새로운 공지가 등록되었습니다.")
		private final String content;

		@Schema(title = "알림 타입", description = "알림 타입입니다.", example = "NOTIFY")
		private final String type;

		@Schema(title = "참고 URI", description = "리다이렉션에 참고할 URI입니다.")
		private final String uri;

		@Schema(title = "전송일시", description = "알림의 전송 일시입니다.")
		private final LocalDateTime sentAt;

		@Schema(title = "읽음여부", description = "회원의 알림 읽음 여부입니다.")
		private final boolean viewed;

		@Builder
		@JsonCreator
		public Send(@JsonProperty("id") Long id, @JsonProperty("content") String content,
			@JsonProperty("type") String type
			, @JsonProperty("uri") String uri, @JsonProperty("sentAt") LocalDateTime sentAt
			, @JsonProperty("viewed") boolean viewed) {
			this.id = id;
			this.content = content;
			this.type = type;
			this.uri = uri;
			this.sentAt = sentAt;
			this.viewed = viewed;
		}
	}
}
