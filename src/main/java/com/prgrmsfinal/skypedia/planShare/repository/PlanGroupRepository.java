package com.prgrmsfinal.skypedia.planShare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroup;

@Repository
public interface PlanGroupRepository extends JpaRepository<PlanGroup, Long> {
	Slice<PlanGroup> findByDeletedFalse(Pageable pageable);

	Slice<PlanGroup> findByRegionNameAndDeletedFalse(String regionName, Pageable pageable);

	Optional<PlanGroup> findByIdAndDeletedFalse(Long id);

	@Query("SELECT pg FROM PlanGroup pg " +
		"LEFT JOIN FETCH pg.member " +
		"LEFT JOIN FETCH pg.region " +
		"LEFT JOIN FETCH pg.planDetails pd " +
		"WHERE pg.id = :groupId")
	Optional<PlanGroup> findByIdWithDetails(@Param("groupId") Long groupId);

	@Query("SELECT pg FROM PlanGroup pg WHERE pg.region.regionName = :regionName")
	List<PlanGroup> findByRegion(@Param("regionName") String regionName);

	@Query("SELECT pg FROM PlanGroup pg WHERE pg.member.username = :username")
	List<PlanGroup> findByMember(@Param("username") String username);

	Page<PlanGroup> findByMemberUsername(String username, Pageable pageable);

	@Query("SELECT pd FROM PlanGroup pd WHERE pd.id = :id AND pd.deleted = :deleted")
	Optional<PlanGroup> findByIdAndDeleted(@Param("id") Long id, @Param("deleted") boolean deleted);

	@Query("SELECT pg FROM PlanGroup pg WHERE pg.id < :lastPlanGroupId AND pg.deleted = :deleted AND pg.member.username = :username ORDER BY pg.id")
	List<PlanGroup> findByUsername(@Param("username") String username, @Param("lastPlanGroupId") Long lastPlanGroupId,
		@Param("deleted") boolean deleted, Pageable pageable);

	@Query(value = "SELECT *, MATCH(title) AGAINST (:keyword) AS relevance FROM plan_group WHERE MATCH(title) AGAINST (:keyword) AND (MATCH(title) AGAINST (:keyword) < :lastRelevance OR (MATCH(title) AGAINST (:keyword) = :lastRelevance AND id < :lastPlanGroupId)) ORDER BY relevance DESC, id DESC LIMIT 10", nativeQuery = true)
	List<PlanGroupResponseDTO.Search> findPlanGroupByTitleKeyword(@Param("keyword") String keyword,
		@Param("lastRelevance") double lastRelevance, @Param("lastPlanGroupId") Long lastPlanGroupId);

	@Query("SELECT DISTINCT pg FROM PlanGroup pg " +
		"LEFT JOIN pg.planDetails pd " +
		"WHERE LOWER(pg.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		"OR LOWER(pd.content) LIKE LOWER(CONCAT('%', :keyword, '%'))"
	)
	Page<PlanGroup> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT pg FROM PlanGroup pg WHERE pg.id < :lastPlanGroupId AND pg.deleted = :deleted AND pg.region.regionName = :regionName ORDER BY pg.id")
	List<PlanGroup> findPlanGroupById(@Param("lastPlanGroupId") Long lastPlanGroupId, @Param("deleted") boolean deleted,
		@Param("regionName") String regionName, Pageable pageable);

	@Modifying
	@Query("UPDATE PlanGroup pg SET pg.views = pg.views + :views WHERE pg.id = :id")
	int incrementViewsById(@Param("id") Long id, @Param("views") Long views);

	@Modifying
	@Query("UPDATE PlanGroup pg SET pg.likes = pg.likes + :likes WHERE pg.id = :id")
	void incrementLikesById(@Param("id") Long id, @Param("likes") Long likes);

	Long findLikesById(Long planGroupId);

	Long findViewsById(Long planGroupId);
}
