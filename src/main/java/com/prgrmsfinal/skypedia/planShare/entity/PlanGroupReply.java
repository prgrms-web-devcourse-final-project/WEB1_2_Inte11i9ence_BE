package com.prgrmsfinal.skypedia.planShare.entity;

import com.prgrmsfinal.skypedia.member.entity.Member;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanGroupReply {

	@EmbeddedId
	private PlanGroupReplyId id;

	@ManyToOne
	@MapsId("planGroupId")
	private PlanGroup planGroup;

	@ManyToOne
	@MapsId("memberId")
	private Member member;
}
