package com.prgrmsfinal.skypedia.planShare.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.planShare.entity.PlanGroupReply;
import com.prgrmsfinal.skypedia.planShare.entity.key.PlanGroupReplyId;
import com.prgrmsfinal.skypedia.reply.entity.Reply;

@Repository
public interface PlanGroupReplyRepository extends JpaRepository<PlanGroupReply, PlanGroupReplyId> {
	@Query("SELECT psr.reply FROM PlanGroupReply psr WHERE psr.planGroup.id = :planGroupId AND psr.reply.id < :lastReplyId ORDER BY psr.repliedAt DESC")
	List<Reply> findRepliesByPlanGroupId(@Param("planGroupId") Long planGroupId, @Param("lastReplyId") Long lastReplyId,
		Pageable pageable);

	@Query("SELECT COUNT(psr) FROM PlanGroupReply psr WHERE psr.planGroup.id = :planGroupId")
	Long countRepliesByPlanGroupId(@Param("planGroupId") Long planGroupId);
}
