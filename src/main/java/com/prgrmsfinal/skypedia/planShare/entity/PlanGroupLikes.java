package com.prgrmsfinal.skypedia.planShare.entity;

import java.time.LocalDateTime;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.planShare.entity.key.PlanGroupLikesId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanGroupLikes {
	@EmbeddedId
	private PlanGroupLikesId id;

	@ManyToOne
	@MapsId("planGroupId")
	@JoinColumn(name = "plan_group_id", referencedColumnName = "id")
	private PlanGroup planGroup;

	@ManyToOne
	@MapsId("memberId")
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	private Member member;

	@Column(insertable = false, updatable = false)
	private LocalDateTime likedAt;
}
