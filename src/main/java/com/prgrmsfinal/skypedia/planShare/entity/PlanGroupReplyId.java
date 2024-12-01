package com.prgrmsfinal.skypedia.planShare.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class PlanGroupReplyId implements Serializable {
	private Long planGroupId;

	private Long memberId;
}
