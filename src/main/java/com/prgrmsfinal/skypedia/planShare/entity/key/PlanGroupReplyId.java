package com.prgrmsfinal.skypedia.planShare.entity.key;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class PlanGroupReplyId implements Serializable {
	private Long planGroupId;

	private Long replyId;
}
