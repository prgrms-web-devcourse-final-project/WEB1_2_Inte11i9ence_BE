package com.prgrmsfinal.skypedia.planShare.scheduler;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroup;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroupLikes;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroupScrap;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupLikesRepository;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupRepository;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupScrapRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanScheduler {

	private final RedisTemplate<String, String> redisTemplate;
	private final PlanGroupRepository planGroupRepository;
	private final MemberRepository memberRepository;
	private final PlanGroupLikesRepository planGroupLikesRepository;
	private final PlanGroupScrapRepository planGroupScrapRepository;

	@Value("${planGroup.views.prefix.key}")
	private String PLAN_GROUP_VIEWS_PREFIX_KEY;

	@Value("${planGroup.likes.prefix.key}")
	private String PLAN_GROUP_LIKES_PREFIX_KEY;

	@Value("${planGroup.unlikes.prefix.key}")
	private String PLAN_GROUP_UNLIKES_PREFIX_KEY;

	@Value("${planGroup.scrap.prefix.key}")
	private String PLAN_GROUP_SCRAP_PREFIX_KEY;

	@Value("${planGroup.unscrap.prefix.key}")
	private String PLAN_GROUP_UNSCRAP_PREFIX_KEY;

	@Transactional
	@Scheduled(fixedRate = 10000)
	public void syncViewsCacheToDB() {
		Map<Object, Object> viewsMap = redisTemplate.opsForHash().entries(PLAN_GROUP_VIEWS_PREFIX_KEY);

		viewsMap.forEach((key, value) -> {
			try {
				Long planGroupId = Long.parseLong(key.toString());
				Long views = Long.parseLong(value.toString());

				int updatedRows = planGroupRepository.incrementViewsById(planGroupId, views);
				if (updatedRows == 0) {
					throw new NoSuchElementException(
						String.format("PlanGroup with ID %d does not exist in the database.", planGroupId));
				}

				log.info("Synced views for PlanGroup ID = {}, Views = {}", planGroupId, views);
			} catch (Exception e) {
				log.warn("Error syncing views to DB: {}", e.getMessage());
			}
		});

		redisTemplate.delete(PLAN_GROUP_VIEWS_PREFIX_KEY);
	}

	@Transactional
	@Scheduled(fixedRate = 10000)
	public void syncLikesCacheToDB() {
		Set<String> keys = redisTemplate.keys(String.format("%s*", PLAN_GROUP_LIKES_PREFIX_KEY));

		if (keys != null && !keys.isEmpty()) {
			keys.forEach(key -> {
				Long planGroupId = extractIdFromKey(key, PLAN_GROUP_LIKES_PREFIX_KEY);
				Set<String> memberIds = redisTemplate.opsForSet().members(key);

				if (memberIds != null) {
					memberIds.forEach(memberIdStr -> {
						Long memberId = Long.parseLong(memberIdStr);
						try {
							if (planGroupLikesRepository.existsByPlanGroupIdAndMemberId(planGroupId, memberId)) {
								log.warn("Like already exists: PlanGroup ID = {}, Member ID = {}", planGroupId,
									memberId);
								return;
							}

							Member member = fetchMember(memberId);
							PlanGroup planGroup = fetchPlanGroup(planGroupId);

							planGroupLikesRepository.save(
								PlanGroupLikes.builder().planGroup(planGroup).member(member).build()
							);

							log.info("Synced like for PlanGroup ID = {}, Member ID = {}", planGroupId, memberId);
						} catch (Exception e) {
							log.warn("Error syncing like to DB: {}", e.getMessage());
						}
					});
				}

				redisTemplate.delete(key);
			});
		}
	}

	@Transactional
	@Scheduled(fixedRate = 10000)
	public void syncScrapCacheToDB() {
		syncScrapKeys(PLAN_GROUP_SCRAP_PREFIX_KEY, true);
		syncScrapKeys(PLAN_GROUP_UNSCRAP_PREFIX_KEY, false);
	}

	private void syncScrapKeys(String prefixKey, boolean isScrap) {
		Set<String> keys = redisTemplate.keys(prefixKey + "*");

		if (keys != null && !keys.isEmpty()) {
			keys.forEach(key -> {
				Long planGroupId = extractIdFromKey(key, prefixKey);
				Set<String> memberIds = redisTemplate.opsForSet().members(key);

				if (memberIds != null) {
					memberIds.forEach(memberIdStr -> {
						Long memberId = Long.parseLong(memberIdStr);

						try {
							if (isScrap) {
								syncScrap(planGroupId, memberId);
							} else {
								syncUnscrap(planGroupId, memberId);
							}
						} catch (Exception e) {
							log.warn("Error syncing {} to DB: {}", isScrap ? "scrap" : "unscrap", e.getMessage());
						}
					});
				}

				redisTemplate.delete(key);
			});
		}
	}

	private void syncScrap(Long planGroupId, Long memberId) {
		if (planGroupScrapRepository.existsByPlanGroupIdAndMemberId(planGroupId, memberId)) {
			throw new IllegalArgumentException(
				String.format("Scrap already exists: PlanGroup ID = %d, Member ID = %d", planGroupId, memberId));
		}

		Member member = fetchMember(memberId);
		PlanGroup planGroup = fetchPlanGroup(planGroupId);

		planGroupScrapRepository.save(
			PlanGroupScrap.builder().planGroup(planGroup).member(member).build()
		);

		log.info("Synced scrap for PlanGroup ID = {}, Member ID = {}", planGroupId, memberId);
	}

	private void syncUnscrap(Long planGroupId, Long memberId) {
		PlanGroupScrap scrap = planGroupScrapRepository.findByPlanGroupIdAndMemberId(planGroupId, memberId)
			.orElseThrow(() -> new IllegalArgumentException(
				String.format("Scrap not found: PlanGroup ID = %d, Member ID = %d", planGroupId, memberId)));

		planGroupScrapRepository.delete(scrap);

		log.info("Synced unscrap for PlanGroup ID = {}, Member ID = {}", planGroupId, memberId);
	}

	private Long extractIdFromKey(String key, String prefix) {
		return Long.parseLong(key.replace(prefix, ""));
	}

	private Member fetchMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new NoSuchElementException(
				String.format("Member not found: ID = %d", memberId)));
	}

	private PlanGroup fetchPlanGroup(Long planGroupId) {
		return planGroupRepository.findById(planGroupId)
			.orElseThrow(() -> new NoSuchElementException(
				String.format("PlanGroup not found: ID = %d", planGroupId)));
	}
}


