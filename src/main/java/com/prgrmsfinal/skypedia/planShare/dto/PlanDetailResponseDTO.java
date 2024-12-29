package com.prgrmsfinal.skypedia.planShare.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PlanDetailResponseDTO {
	@Getter
	@Builder
	@AllArgsConstructor
	@Schema(title = "PlanDetail 응답 DTO", description = "세부 일정 응답 DTO입니다.")
	public static class ReadAll {
		@Schema(title = "세부 일정 ID", description = "세부 일정의 고유 ID입니다.", example = "10")
		private final Long id;

		@Schema(title = "다음 세부 일정", description = "현재 세부 일정의 다음 세부 일정입니다.")
		private final PlanDetailResponseDTO.Read nextPlanDetail;

		@Schema(title = "이전 세부 일정", description = "현재 세부 일정의 이전 세부 일정입니다.")
		private final PlanDetailResponseDTO.Read prePlanDetail;

		@Schema(title = "장소명", description = "세부 일정의 장소명입니다.", example = "명동 쇼핑거리")
		private final String location;

		@Schema(title = "placeId", description = "Google Maps API에서 제공하는 placeId입니다.", example = "ChIJd_Y0eVIvkFQR0iyWr8zHAAQ")
		private final String placeId;

		@Schema(title = "장소 설명", description = "세부 일정의 장소에 대한 설명입니다.", example = "서울 명동 쇼핑 거리입니다.")
		private final String content;

		@Schema(title = "위도", description = "장소의 위도입니다.", example = "37.5665")
		private final Double latitude;

		@Schema(title = "경도", description = "장소의 경도입니다.", example = "126.9780")
		private final Double longitude;

		@Schema(title = "장소 이미지 URL", description = "장소를 대표하는 이미지 URL입니다.", example = "https://example.com/image.jpg")
		private final String locationImage;

		@Schema(title = "방문 날짜", description = "장소 방문 날짜입니다.", example = "2024-12-02")
		private final LocalDate planDate;

		@Schema(title = "수정 일자", description = "해당 세부 일정의 수정 일자입니다.")
		private final LocalDateTime updatedAt;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@Schema(title = "PlanDetail 응답 DTO", description = "세부 일정 응답 DTO입니다.")
	public static class Read {

		@Schema(title = "세부 일정 ID", description = "세부 일정의 고유 ID입니다.", example = "10")
		private final Long id;

		@Schema(title = "다음 세부 일정", description = "현재 세부 일정의 다음 세부 일정입니다.")
		private final PlanDetailResponseDTO.Read nextPlanDetail;

		@Schema(title = "이전 세부 일정", description = "현재 세부 일정의 이전 세부 일정입니다.")
		private final PlanDetailResponseDTO.Read prePlanDetail;

		@Schema(title = "장소명", description = "세부 일정의 장소명입니다.", example = "명동 쇼핑거리")
		private final String location;

		@Schema(title = "placeId", description = "Google Maps API에서 제공하는 placeId입니다.", example = "ChIJd_Y0eVIvkFQR0iyWr8zHAAQ")
		private final String placeId;

		@Schema(title = "장소 설명", description = "세부 일정의 장소에 대한 설명입니다.", example = "서울 명동 쇼핑 거리입니다.")
		private final String content;

		@Schema(title = "위도", description = "장소의 위도입니다.", example = "37.5665")
		private final Double latitude;

		@Schema(title = "경도", description = "장소의 경도입니다.", example = "126.9780")
		private final Double longitude;

		@Schema(title = "장소 이미지 URL", description = "장소를 대표하는 이미지 URL입니다.", example = "https://example.com/image.jpg")
		private final String locationImage;

		@Schema(title = "방문 날짜", description = "장소 방문 날짜입니다.", example = "2024-12-02")
		private final LocalDate planDate;
	}

	@Getter
	@AllArgsConstructor
	@Builder
	public static class Info {

		@Schema(title = "세부 일정 ID", description = "세부 일정의 ID입니다.")
		private final Long id;

		@Schema(title = "세부 일정이 속한 일정 그룹 ID", description = "세부 일정이 속한 일정 그룹의 ID입니다.")
		private final Long planGroupId;

		@Schema(title = "다음 세부 일정", description = "다음 순서의 세부 일정입니다.")
		private final PlanDetailResponseDTO.Info nextPlanDetail;

		@Schema(title = "이전 세부 일정", description = "이전 순서의 세부 일정입니다.")
		private final PlanDetailResponseDTO.Info prePlanDetail;

		@Schema(title = "장소명", description = "사용자가 방문한 장소명입니다.")
		private final String location;

		@Schema(title = "장소의 구글 지도 ID", description = "사용자가 방문한 장소에 해당하는 구글 지도 ID입니다.")
		private final String placeId;

		@Schema(title = "장소 내용", description = "사용자가 방문한 장소에 대한 메모 내용입니다.")
		private final String content;

		@Schema(title = "위도", description = "사용자가 방문한 장소의 위도값입니다.")
		private final Double latitude;

		@Schema(title = "경도", description = "사용자가 방문한 장소의 경도값입니다.")
		private final Double longitude;

		@Schema(title = "이미지 URL", description = "사용자가 방문한 장소의 대표 이미지 URL입니다.")
		private final String locationImage;

		@Schema(title = "방문 일자", description = "사용자가 해당 장소를 방문한 일자입니다.")
		private final LocalDate planDate;

		@Schema(title = "수정 일자", description = "게시물이 마지막으로 수정된 날짜입니다.")
		private final LocalDateTime updateAt;
	}
}
