package com.prgrmsfinal.skypedia.planShare.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import lombok.Setter;

@Entity
@Getter
@Setter
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
	@JsonManagedReference
	private PlanDetail nextPlanDetail;  // 이전 세부 일정

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_detail_prev_id")
	@JsonBackReference
	private PlanDetail prePlanDetail;   // 이후 세부 일정

	private String location;            // 장소명

	private String placeId;             // 구글 지도 API 관련 ID

	private String content;             // 장소 설명

	@Column(columnDefinition = "POINT")
	private Point coordinates;          // 공간 정보 (위도/경도)

	private String locationImage;       // 장소 이미지

	private LocalDate planDate;         // 장소 방문 날짜

	@Builder.Default
	private Boolean deleted = false;    // 논리 삭제 여부

	private LocalDateTime deletedAt;    // 세부 일정 삭제 일자

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	public void validateCoordinates(double latitude, double longitude) {
		if (latitude < -90 || latitude > 90) {
			throw new IllegalArgumentException("위도 값은 -90 ~ 90 사이여야 합니다.");
		}
		if (longitude < -180 || longitude > 180) {
			throw new IllegalArgumentException("경도 값은 -180 ~ 180 사이여야 합니다.");
		}
	}

	public void updateCoordinates(double latitude, double longitude) {
		validateCoordinates(latitude, longitude);
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		this.coordinates = geometryFactory.createPoint(new Coordinate(longitude, latitude));
	}

	// 좌표 조회 편의 메서드 추가
	public double getLatitude() {
		return coordinates != null ? coordinates.getY() : 0;
	}

	public double getLongitude() {
		return coordinates != null ? coordinates.getX() : 0;
	}

	public void updateDetails(String content, LocalDate planDate, Point coordinates, String location,
		String locationImage) {
		this.content = content;
		this.coordinates = coordinates;
		this.planDate = planDate;
		this.location = location;
		this.locationImage = locationImage;
	}

	// 다음 Node 연결 메서드
	public void linkNext(PlanDetail nextDetail) {
		if (nextDetail == this) {
			throw new IllegalArgumentException("스스로는 다음 일정이 될 수 없습니다.");
		}
		// 기존 연결 해제
		if (this.nextPlanDetail != null) {
			this.nextPlanDetail.prePlanDetail = null;
		}
		if (nextDetail != null && nextDetail.prePlanDetail != null) {
			nextDetail.prePlanDetail.nextPlanDetail = null;
		}

		// 새로운 연결 설정
		this.nextPlanDetail = nextDetail;
		if (nextDetail != null) {
			nextDetail.prePlanDetail = this;
		}
	}

	// 일정 삭제
	public void removePlanDetail() {
		// 이전 일정과 다음 일정을 서로 연결
		if (this.prePlanDetail != null) {
			this.prePlanDetail.nextPlanDetail = this.nextPlanDetail;
		}
		if (this.nextPlanDetail != null) {
			this.nextPlanDetail.prePlanDetail = this.prePlanDetail;
		}

		// 현재 일정의 연결 해제
		this.prePlanDetail = null;
		this.nextPlanDetail = null;

		// soft delete 처리
		this.deleted = true;
		this.deletedAt = LocalDateTime.now();
	}

	public void moveAfter(PlanDetail targetDetail) {
		if (targetDetail == null) {
			throw new IllegalArgumentException("대상 일정이 null일 수 없습니다.");
		}
		if (targetDetail == this) {
			throw new IllegalArgumentException("자기 자신의 뒤로 이동할 수 없습니다.");
		}

		// 순환 참조 검사
		PlanDetail current = targetDetail;
		while (current != null) {
			if (current.getPrePlanDetail() == this) {
				throw new IllegalArgumentException("순환 참조가 발생할 수 있는 이동은 불가능합니다.");
			}
			current = current.getPrePlanDetail();
		}

		// 현재 위치에서 제거
		removePlanDetail();

		// 타겟의 다음 일정과 연결
		PlanDetail oldNext = targetDetail.nextPlanDetail;
		targetDetail.nextPlanDetail = this;
		this.prePlanDetail = targetDetail;

		if (oldNext != null) {
			oldNext.prePlanDetail = this;
			this.nextPlanDetail = oldNext;
		}
	}
}
