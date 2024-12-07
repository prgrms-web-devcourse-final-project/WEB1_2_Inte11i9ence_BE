package com.prgrmsfinal.skypedia.planShare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanDetail;

@Repository
public interface PlanDetailRepository extends JpaRepository<PlanDetail, Long> {
	@Query(value = "SELECT *, MATCH(content) AGAINST (:keyword) AS relevance FROM post WHERE MATCH(content) AGAINST (:keyword) AND (MATCH(content) AGAINST (:keyword) < :lastRelevance OR (MATCH(content) AGAINST (:keyword) = :lastRelevance = :lastRelevance AND id < :lastPostId)) ORDER BY relevance DESC, id DESC LIMIT 10", nativeQuery = true)
	List<PlanGroupResponseDTO.Search> findPlanGroupByContentKeyword(@Param("keyword") String keyword,
		@Param("lastRelevance") double lastRelevance,
		@Param("lastPostId") Long lastPlanGroupId);
}
