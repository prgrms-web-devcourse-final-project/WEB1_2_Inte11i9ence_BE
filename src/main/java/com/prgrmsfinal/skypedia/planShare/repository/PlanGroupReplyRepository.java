package com.prgrmsfinal.skypedia.planShare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrmsfinal.skypedia.planShare.entity.PlanGroupReply;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroupReplyId;

public interface PlanGroupReplyRepository extends JpaRepository<PlanGroupReply, PlanGroupReplyId> {
}
