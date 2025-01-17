package com.prgrmsfinal.skypedia.planShare.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PlanGroupRequestDTO {
	@Schema(title = "일정 그룹 생성 요청 DTO", description = "일정 그룹 생성 요청을 위한 DTO입니다.")
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Create {

		@Schema(title = "일정 그룹 제목", description = "일정 그룹 게시물의 제목입니다.")
		@NotBlank(message = "제목 입력은 필수입니다.")
		@Length(max = 50, message = "제목은 50자까지 입력 가능합니다.")
		@Pattern(regexp = "^(?!\\s).*(?<!\\s)$", message = "제목의 맨 앞과 뒤에는 공백이 들어갈 수 없습니다.")
		private String title;

		@Schema(title = "지역 카테고리 이름", description = "게시물이 속할 지역 카테고리 이름입니다.")
		@NotNull(message = "지역 카테고리는 비워둘 수 없습니다.")
		private String regionName;

		@Schema(title = "대표 이미지 URL", description = "일정 그룹의 대표 이미지입니다.", example = "https://example.com/image.jpg")
		@NotBlank(message = "대표 이미지 URL은 비워둘 수 없습니다.")
		private String groupImage;

		@Schema(title = "세부 일정 리스트", description = "일정 그룹에 포함될 세부 일정 정보입니다.")
		private List<PlanDetailRequestDTO.Create> planDetails;
	}

	@Schema(title = "게시글 댓글 등록 요청 DTO", description = "게시글에 댓글 등록 요청에 사용하는 DTO입니다.")
	@Getter
	public static class CreateReply {
		@Schema(title = "부모 댓글 ID", description = "대댓글의 경우 필요한 부모 댓글의 ID입니다.", example = "1")
		@Min(value = 1, message = "ID는 0 이하일 수 없습니다.")
		private final Long parentId;

		@Schema(title = "댓글 내용", description = "댓글 내용입니다.", example = "댓글 내용")
		@Length(max = 1000, message = "댓글은 1000자를 초과할 수 없습니다.")
		private final String content;

		@JsonCreator
		public CreateReply(@JsonProperty("parentId") Long parentId, @JsonProperty("content") String content) {
			this.parentId = parentId;
			this.content = content;
		}
	}

	@Schema(title = "PlanGroup 수정 요청 DTO", description = "일정 그룹 수정을 위한 요청 DTO입니다.")
	@Data
	@AllArgsConstructor
	public class Update {
		@Schema(title = "일정 제목", description = "수정할 일정 그룹의 제목입니다.", example = "가족 여행")
		@NotBlank(message = "일정 제목은 비워둘 수 없습니다.")
		@Length(max = 20, message = "일정 제목은 최대 20자까지 입력 가능합니다.")
		private final String title;

		@Schema(title = "대표 이미지 URL", description = "수정할 일정 그룹의 대표 이미지입니다.", example = "https://example.com/image.jpg")
		private final String groupImage;

		@Schema(title = "세부 일정 LinkedList", description = "수정할 세부 일정 목록입니다.")
		private final List<PlanDetailRequestDTO.Update> planDetails;
	}
}
