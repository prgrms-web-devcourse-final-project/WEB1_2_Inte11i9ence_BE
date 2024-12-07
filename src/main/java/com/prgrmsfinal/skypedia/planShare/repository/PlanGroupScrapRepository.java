package com.prgrmsfinal.skypedia.planShare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.planShare.entity.PlanGroupScrap;
import com.prgrmsfinal.skypedia.planShare.entity.key.PlanGroupScrapId;

@Repository
public interface PlanGroupScrapRepository extends JpaRepository<PlanGroupScrap, PlanGroupScrapId> {
	@Query(value = "SELECT EXISTS (SELECT 1 FROM plan_group_scrap WHERE plan_group_id = :id AND member_id = :memberId)", nativeQuery = true)
	boolean existsByPlanGroupIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

	@Query("SELECT pgs FROM PlanGroupScrap pgs WHERE pgs.planGroup.id = :id AND pgs.member.id = :memberId")
	Optional<PlanGroupScrap> findByPlanGroupIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);
}
