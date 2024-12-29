package com.prgrmsfinal.skypedia.planShare.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
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

	/**
	 * PlanGroup 조회수 동기화
	 */
	@Transactional
	@Scheduled(fixedRate = 10000)
	public void syncViewsCacheToDB() {
		Map<Object, Object> viewsMap = redisTemplate.opsForHash().entries(PLAN_GROUP_VIEWS_PREFIX_KEY);

		viewsMap.entrySet().forEach(e -> {
			Long id = Long.parseLong(e.getKey().toString());
			Long views = Long.parseLong(e.getValue().toString());

			if (planGroupRepository.incrementViewsById(id, views) == 0) {
				log.warn("DB Sync error: PlanGroup not exists. (planGroupId={}, views={})", id, views);
			}
		});

		redisTemplate.delete(PLAN_GROUP_VIEWS_PREFIX_KEY);
	}

	@Transactional
	@Scheduled(fixedRate = 10000)
	public void syncLikesCacheToDB() {
		Set<String> keySet = redisTemplate.keys(StringUtils.join(PLAN_GROUP_LIKES_PREFIX_KEY, "*"));

		if (keySet != null) {
			keySet.forEach(key -> {
				Long planGroupId = Long.parseLong(key.replace(PLAN_GROUP_LIKES_PREFIX_KEY, ""));
				AtomicLong likes = new AtomicLong(redisTemplate.opsForSet().size(key));
				Set<String> values = redisTemplate.opsForSet().members(key);

				values.forEach(value -> {
					Long memberId = Long.parseLong(value);

					try {
						if (planGroupLikesRepository.existsByPlanGroupIdAndMemberId(planGroupId, memberId)) {
							likes.decrementAndGet();
							throw new IllegalArgumentException(StringUtils.join(
								"Data already exists. [planGroupId=", planGroupId, ", memberId=", memberId, "]"));
						}

						Member member = memberRepository.findById(memberId).orElseThrow(() ->
							new NoSuchElementException(
								StringUtils.join("Invalid member id. [memberId=", memberId, "]")));

						PlanGroup planGroup = planGroupRepository.findById(planGroupId).orElseThrow(() ->
							new NoSuchElementException(
								StringUtils.join("Invalid planGroup id. [planGroupId=", planGroupId, "]")));

						planGroupLikesRepository.save(PlanGroupLikes.builder()
							.planGroup(planGroup)
							.member(member)
							.build());
					} catch (IllegalArgumentException | NoSuchElementException e) {
						log.warn("DB Sync error: {}", e.getMessage());
					}
				});

				redisTemplate.delete(StringUtils.join(PLAN_GROUP_LIKES_PREFIX_KEY, planGroupId));
				planGroupRepository.incrementLikesById(planGroupId, likes.get());
			});
		}
	}

	@Transactional
	@Scheduled(fixedRate = 10000)
	public void syncScrapCacheToDB() {
		Set<String> scrapKeys = redisTemplate.keys(PLAN_GROUP_SCRAP_PREFIX_KEY + "*");
		Set<String> unscrapKeys = redisTemplate.keys(PLAN_GROUP_UNSCRAP_PREFIX_KEY + "*");

		if (scrapKeys != null) {
			scrapKeys.forEach(key -> {
				Long planGroupId = Long.parseLong(key.replace(PLAN_GROUP_SCRAP_PREFIX_KEY, ""));
				Set<String> values = redisTemplate.opsForSet().members(key);
				List<PlanGroupScrap> planGroupScraps = new ArrayList<>();

				values.forEach(value -> {
					Long memberId = Long.parseLong(value);

					try {
						if (planGroupScrapRepository.existsByPlanGroupIdAndMemberId(planGroupId, memberId)) {
							throw new IllegalArgumentException(StringUtils.join(
								"Data already exists. [planGroupId=", planGroupId, ", memberId=", memberId, "]"));
						}

						Member member = memberRepository.findById(memberId).orElseThrow(() ->
							new NoSuchElementException(
								StringUtils.join("Invalid member id. [memberId=", memberId, "]")));

						PlanGroup planGroup = planGroupRepository.findById(planGroupId).orElseThrow(() ->
							new NoSuchElementException(
								StringUtils.join("Invalid planGroup id. [planGroupId=", planGroupId, "]")));

						planGroupScraps.add(PlanGroupScrap.builder()
							.planGroup(planGroup)
							.member(member)
							.build());
					} catch (IllegalArgumentException | NoSuchElementException e) {
						log.warn("DB Sync error: {}", e.getMessage());
					}
				});

				redisTemplate.delete(PLAN_GROUP_SCRAP_PREFIX_KEY + planGroupId);
				planGroupScrapRepository.saveAll(planGroupScraps);
			});
		}

		if (unscrapKeys != null) {
			unscrapKeys.forEach(key -> {
				Long planGroupId = Long.parseLong(key.replace(PLAN_GROUP_UNSCRAP_PREFIX_KEY, ""));
				Set<String> values = redisTemplate.opsForSet().members(key);

				values.forEach(value -> {
					Long memberId = Long.parseLong(value);

					try {
						if (!planGroupScrapRepository.existsByPlanGroupIdAndMemberId(planGroupId, memberId)) {
							throw new IllegalArgumentException(StringUtils.join(
								"Data not exists. [planGroupId=", planGroupId, ", memberId=", memberId, "]"));
						}

						planGroupScrapRepository.delete(
							planGroupScrapRepository.findByPlanGroupIdAndMemberId(planGroupId, memberId)
								.orElseThrow(() -> new NoSuchElementException(StringUtils.join(
									"Invalid memberId or planGroupId. [planGroupId=", planGroupId, ", memberId=",
									memberId, "]"))));
					} catch (IllegalArgumentException | NoSuchElementException e) {
						log.warn("DB Sync error: {}", e.getMessage());
					}
				});

				redisTemplate.delete(StringUtils.join(PLAN_GROUP_UNSCRAP_PREFIX_KEY, planGroupId));
			});
		}
	}
}


