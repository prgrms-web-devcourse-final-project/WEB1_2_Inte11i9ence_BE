package com.prgrmsfinal.skypedia.planShare.entity;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.prgrmsfinal.skypedia.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;              // 해당 게시물의 회원 ID

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id")
	private Region region;              // 일정의 지역 ID

	@OneToMany(mappedBy = "planGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<PlanDetail> planDetails = new LinkedList<>();  // 세부 일정

	private String title;               // 일정 제목

	private String groupImage;          // 일정 대표 이미지

	@Builder.Default
	private Long views = 0L;             // 조회수

	@Builder.Default
	private Long likes = 0L;             // 좋아요 수

	@Builder.Default
	private Boolean deleted = false;     // 논리 삭제 여부

	private LocalDateTime deletedAt;    // 게시물 삭제 일자

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	public void modify(String title, String groupImage) {
		this.title = title;
		this.groupImage = groupImage;
	}
}
