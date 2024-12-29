package com.prgrmsfinal.skypedia.planShare.dto;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PlanDetailRequestDTO {
	@Getter
	@AllArgsConstructor
	@Schema(title = "세부 일정 DTO", description = "일정 그룹 게시물에 포함되는 세부 일정 DTO입니다.")
	public static class Create {

		@Schema(title = "다음 세부 일정", description = "현재 세부 일정의 다음 세부 일정입니다.")
		private final PlanDetailRequestDTO.Create nextPlanDetail;

		@Schema(title = "이전 세부 일정", description = "현재 세부 일정의 이전 세부 일정입니다.")
		private final PlanDetailRequestDTO.Create prePlanDetail;

		@Schema(title = "장소명", description = "사용자가 방문한 장소의 이름입니다.")
		@NotBlank(message = "세부 일정의 장소명 입력은 필수입니다.")
		private final String location;

		@Schema(title = "장소 ID", description = "Google Place API의 ID값입니다.")
		private final String placeId;

		@Schema(title = "장소 메모", description = "사용자가 방문한 장소에 대한 짧은 메모입니다.")
		private final String content;

		@Schema(title = "위도", description = "장소의 위도 좌표입니다.")
		@NotNull(message = "위도 입력은 필수입니다.")
		@DecimalMax(value = "90.0")
		@DecimalMin(value = "-90.0")
		private final Double latitude;

		@Schema(title = "경도", description = "장소의 경도 좌표입니다.")
		@NotNull(message = "경도 입력은 필수입니다.")
		@DecimalMax(value = "90.0")
		@DecimalMin(value = "-90.0")
		private final Double longitude;

		@Schema(title = "이미지 URL", description = "장소의 대표 이미지 URL입니다.")
		@NotBlank(message = "장소 이미지는 필수값입니다.")
		private final String locationImage;

		@Schema(title = "방문 일자", description = "사용자가 해당 장소를 방문한 일자입니다.")
		@NotNull(message = "방문 일자 입력은 필수입니다.")
		private final LocalDate planDate;
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@Schema(title = "PlanDetail 수정 요청 DTO", description = "수정할 세부 일정 정보입니다.")
	public static class Update {

		@Schema(title = "세부 일정 ID", description = "세부 일정의 ID입니다.")
		private final Long id;

		@Schema(title = "장소명", description = "수정할 세부 일정의 장소명입니다.", example = "서울특별시 강남구")
		private final String location;

		@Schema(title = "지역 카테고리", description = "수정할 지역 카테고리입니다.")
		private final String regionName;

		@Schema(title = "장소 설명", description = "수정할 세부 일정의 장소 설명입니다.", example = "명동 쇼핑거리")
		@Length(max = 500, message = "내용은 500자까지 입력 가능합니다.")
		private final String content;

		@Schema(title = "위도", description = "수정할 세부 일정의 위도입니다.", example = "37.5665")
		private final Double latitude;

		@Schema(title = "경도", description = "수정할 세부 일정의 경도입니다.", example = "126.9780")
		private final Double longitude;

		@Schema(title = "장소 이미지 URL", description = "수정할 장소의 이미지입니다.", example = "https://example.com/photo.jpg")
		private final String locationImage;

		@Schema(title = "방문 날짜", description = "수정할 세부 일정의 방문 날짜입니다.", example = "2024-12-02")
		private final LocalDate planDate;
	}
}
