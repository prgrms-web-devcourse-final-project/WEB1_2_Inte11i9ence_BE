package com.prgrmsfinal.skypedia.planShare.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PlanGroupReplyId implements Serializable {
    private Long planGroupId;

    private Long memberId;
}
