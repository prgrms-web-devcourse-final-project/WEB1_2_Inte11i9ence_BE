package com.prgrmsfinal.skypedia.planShare.entity.key;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class PlanGroupLikesId implements Serializable {
	private Long planGroupId;

	private Long memberId;
}
