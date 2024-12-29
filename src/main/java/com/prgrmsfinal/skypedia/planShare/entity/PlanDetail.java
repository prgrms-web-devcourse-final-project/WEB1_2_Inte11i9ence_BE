package com.prgrmsfinal.skypedia.planShare.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;                    // 세부 일정 ID

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_group_id", nullable = false)
	private PlanGroup planGroup;        // 세부 일정이 속한 그룹 ID

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_detail_next_id")
	private PlanDetail nextPlanDetail;  // 이전 세부 일정

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_detail_prev_id")
	private PlanDetail prePlanDetail;   // 이후 세부 일정

	private String location;            // 장소명

	private String placeId;

	private String content;             // 장소 설명

	private Double latitude;            // 위도

	private Double longitude;           // 경도

	private String locationImage;       // 장소 이미지

	private LocalDate planDate;         // 장소 방문 날짜

	@Builder.Default
	private Boolean deleted = false;    // 논리 삭제 여부

	private LocalDateTime deletedAt;    // 세부 일정 삭제 일자

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	public void updateCoordinates(Double latitude, Double longitude) {
		if (latitude < -90 || latitude > 90) {
			throw new IllegalArgumentException("위도 값은 -90 ~ 90 사이여야 합니다.");
		}
		if (longitude < -180 || longitude > 180) {
			throw new IllegalArgumentException("경도 값은 -180 ~ 180 사이여야 합니다.");
		}
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public void updateDetails(String content, LocalDate planDate, Double latitude, Double longitude, String location,
		String locationImage) {
		this.content = content;
		this.planDate = planDate;
		this.latitude = latitude;
		this.longitude = longitude;
		this.location = location;
		this.locationImage = locationImage;
	}
}
