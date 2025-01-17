package com.prgrmsfinal.skypedia.planShare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.planShare.entity.PlanGroupLikes;
import com.prgrmsfinal.skypedia.planShare.entity.key.PlanGroupLikesId;
import com.prgrmsfinal.skypedia.post.entity.PostLikes;

@Repository
public interface PlanGroupLikesRepository extends JpaRepository<PlanGroupLikes, PlanGroupLikesId> {
	@Query(value = "SELECT EXISTS (SELECT 1 FROM plan_group_likes WHERE id = :id AND member_id = :memberId)", nativeQuery = true)
	boolean existsByPlanGroupIdAndMemberId(@Param("id") Long planGroupId, @Param("memberId") Long memberId);

	@Query("SELECT psl FROM PlanGroupLikes psl WHERE psl.planGroup.id = :planGroupId AND psl.member.id = :memberId")
	Optional<PostLikes> findByPlanGroupIdAndMemberId(@Param("planGroupId") Long postId,
		@Param("memberId") Long memberId);
}
