package com.prgrmsfinal.skypedia.planShare.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.prgrmsfinal.skypedia.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "plan_group")
public class PlanGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;                    // 일정 그룹 ID

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;              // 해당 게시물의 회원 ID

	@ManyToOne
	@JoinColumn(name = "region_id")
	private Region region;              // 일정의 지역 ID

	@OneToMany(mappedBy = "planGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlanDetail> planDetails = new ArrayList<>();  // 세부 일정

	private String title;               // 일정 제목

	// private String groupImage;          // 일정 대표 이미지

	@Builder.Default
	private Long views = 0L;            // 조회수

	@Builder.Default
	private Long likes = 0L;            // 좋아요 수

	@Builder.Default
	private Boolean deleted = false;    // 논리 삭제 여부

	private LocalDateTime deletedAt;    // 게시물 삭제 일자

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	public void addPlanDetail(PlanDetail planDetail) {
		planDetails.add(planDetail);
		planDetail.setPlanGroup(this);
	}

	public void removePlanDetail(PlanDetail planDetail) {
		planDetails.remove(planDetail);
		planDetail.setPlanGroup(null);
	}

	public PlanDetail getFirstPlanDetail() {
		return planDetails.stream()
			.filter(pd -> !pd.getDeleted() && pd.getPrePlanDetail() == null)
			.findFirst()
			.orElse(null);
	}

	public PlanDetail getLastPlanDetail() {
		return planDetails.stream()
			.filter(pd -> !pd.getDeleted() && pd.getNextPlanDetail() == null)
			.findFirst()
			.orElse(null);
	}

	public void delete() {
		this.deleted = true;
		this.deletedAt = LocalDateTime.now();
		this.planDetails.forEach(PlanDetail::removePlanDetail);
	}

	public void update(String title, String groupImage, Region region) {
		this.title = title;
		// this.groupImage = groupImage;
		this.region = region;
	}

	public void restore() {
		this.deleted = false;
		this.deletedAt = null;
	}
}
