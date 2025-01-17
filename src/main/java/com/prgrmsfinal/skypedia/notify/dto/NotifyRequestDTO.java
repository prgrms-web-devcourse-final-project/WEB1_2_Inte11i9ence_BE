package com.prgrmsfinal.skypedia.notify.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.notify.constant.NotifyType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(title = "알림 요청 DTO", description = "알림 요청에 사용하는 DTO 입니다.")
public class NotifyRequestDTO {
	@Getter
	@Schema(title = "특정 회원 알림 요청 DTO", description = "특정 회원 알림 요청에 사용하는 DTO 입니다.")
	public static class User {
		@Schema(title = "회원", description = "알림 수신자의 회원 정보를 담는 객체입니다.")
		private final Member member;

		@Schema(title = "알림 내용", description = "알림 내용입니다.", example = "새로운 공지가 등록되었습니다.")
		private final String content;

		@Schema(title = "알림 타입", description = "알림 타입입니다.", example = "NOTIFY")
		private final NotifyType notifyType;

		@Schema(title = "참고 URI", description = "리다이렉션에 참고할 URI입니다.")
		private final String uri;

		@Schema(title = "전송일시", description = "알림의 전송 일시입니다.")
		private final LocalDateTime sentAt;

		@JsonCreator
		public User(@JsonProperty("member") Member member, @JsonProperty("content") String content
			, @JsonProperty("notifyType") NotifyType notifyType, @JsonProperty("uri") String uri
			, @JsonProperty("sentAt") LocalDateTime sentAt) {
			this.member = member;
			this.content = content;
			this.notifyType = notifyType;
			this.uri = uri;
			this.sentAt = sentAt;
		}
	}

	@Getter
	@Schema(title = "전체 회원 알림 요청 DTO", description = "전체 회원 알림 요청에 사용하는 DTO 입니다.")
	public static class Global {
		@Schema(title = "알림 내용", description = "알림 내용입니다.", example = "새로운 공지가 등록되었습니다.")
		private final String content;

		@Schema(title = "알림 타입", description = "알림 타입입니다.", example = "NOTIFY")
		private final NotifyType notifyType;

		@Schema(title = "참고 URI", description = "리다이렉션에 참고할 URI입니다.")
		private final String uri;

		@Schema(title = "전송일시", description = "알림의 전송 일시입니다.")
		private final LocalDateTime sentAt;

		public Global(@JsonProperty("content") String content, @JsonProperty("notifyType") NotifyType notifyType
			, @JsonProperty("uri") String uri, @JsonProperty("sentAt") LocalDateTime sentAt) {
			this.content = content;
			this.notifyType = notifyType;
			this.uri = uri;
			this.sentAt = sentAt;
		}
	}
}
