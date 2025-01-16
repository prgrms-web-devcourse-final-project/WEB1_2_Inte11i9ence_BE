package com.prgrmsfinal.skypedia.planShare.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroup;
import com.prgrmsfinal.skypedia.planShare.exception.PlanError;
import com.prgrmsfinal.skypedia.planShare.mapper.PlanGroupMapper;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupLikesRepository;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupRepository;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupScrapRepository;
import com.prgrmsfinal.skypedia.post.exception.PostError;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import com.prgrmsfinal.skypedia.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanGroupServiceImpl implements PlanGroupService {
	private final RedisTemplate<String, String> redisTemplate;
	private final PlanGroupReplyService planGroupReplyService;
	private final MemberService memberService;
	private final RegionService regionService;
	private final PlanGroupRepository planGroupRepository;
	private final PlanGroupLikesRepository planGroupLikesRepository;
	private final PlanGroupScrapRepository planGroupScrapRepository;
	private final ReplyService replyService;

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

	@Override
	public PlanGroupResponseDTO.ReadAll readAll(Authentication authentication, String standard, String regionName,
		int page) {
		Pageable pageable = PageRequest.of(page, 10, getSortByOrder(standard)); // 한 페이지당 10개 게시물
		Slice<PlanGroup> planGroups;

		if (StringUtils.isNotBlank(regionName)) {
			planGroups = planGroupRepository.findByRegionNameAndDeletedFalse(regionName, pageable);
		} else {
			planGroups = planGroupRepository.findByDeletedFalse(pageable);
		}

		if (planGroups.isEmpty()) {
			return new PlanGroupResponseDTO.ReadAll(Collections.emptyList(), null);
		}

		List<PlanGroupResponseDTO.Info> responseList = planGroups.stream()
			.map(PlanGroupMapper::toInfo)
			.toList();

		String nextUri = planGroups.hasNext()
			? "/api/v1/plan-group?page=" + (page + 1) + "&standard=" + standard + (regionName != null
			? "&region=" + regionName : "")
			: null;

		return new PlanGroupResponseDTO.ReadAll(responseList, nextUri);
	}

	private Sort getSortByOrder(String order) {
		return switch (order) {
			case "likes" -> Sort.by(Sort.Order.desc("likes"), Sort.Order.desc("id"));
			case "views" -> Sort.by(Sort.Order.desc("views"), Sort.Order.desc("id"));
			default -> Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"));
		};
	}

	@Override
	public PlanGroupResponseDTO.Read read(Authentication authentication, Long planGroupId) {
		PlanGroup planGroup = planGroupRepository.findByIdAndDeletedFalse(planGroupId)
			.orElseThrow(() -> new RuntimeException("해당 ID에 대한 PlanGroup이 존재하지 않습니다."));

		return PlanGroupMapper.toRead(
			planGroup,
			new MemberResponseDTO.Info(planGroup.getMember().getId(), planGroup.getMember().getUsername(),
				planGroup.getMember().getProfileImage()),
			new PlanGroupResponseDTO.Statistics(planGroup.getViews(), planGroup.getLikes(), false, false),
			Collections.emptyList(),
			null,
			Collections.emptyList()
		);
	}

	@Override
	public void create(Authentication authentication, PlanGroupRequestDTO.Create groupCreate) {
		Member member = getAuthenticatedMember(authentication);

		PlanGroup newPlanGroup = PlanGroup.builder()
			.member(member)
			.title(groupCreate.getTitle())
			.build();

		planGroupRepository.save(newPlanGroup);
	}

	private Member getAuthenticatedMember(Authentication authentication) {
		return (Member)authentication.getPrincipal();
	}

	@Override
	public String update(Authentication authentication, Long id, PlanGroupRequestDTO.Update groupUpdate) {
		PlanGroup planGroup = planGroupRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 PlanGroup ID입니다."));

		Member authenticatedMember = getAuthenticatedMember(authentication);
		if (!planGroup.getMember().getId().equals(authenticatedMember.getId())) {
			throw new SecurityException("수정 권한이 없습니다.");
		}

		planGroupRepository.save(planGroup);

		return null;
	}

	@Override
	public void delete(Authentication authentication, Long planGroupId) {
		PlanGroup planGroup = planGroupRepository.findById(planGroupId)
			.orElseThrow(PlanError.NOT_FOUND::getException);

		Member authenticatedMember = getAuthenticatedMember(authentication);
		if (!planGroup.getMember().getId().equals(authenticatedMember.getId())) {
			throw new SecurityException("삭제 권한이 없습니다."); // 권한 없음 예외
		}

		planGroupRepository.delete(planGroup);
	}

	@Override
	public void restore(Authentication authentication, Long planGroupId) {

	}

	@Override
	public PlanGroupResponseDTO.ReadAll readByMember(String username, int page) {
		Pageable pageable = PageRequest.of(page, 10, Sort.by("updatedAt").descending());

		Slice<PlanGroup> planGroups = planGroupRepository.findByMemberUsername(username, pageable);

		List<PlanGroupResponseDTO.Info> planShare = planGroups.getContent().stream()
			.map(PlanGroupMapper::toInfo) // 기존 toRead 대신 toInfo로 매핑
			.toList();

		String nextUri = planGroups.hasNext()
			? "/api/v1/plan-group/member?username=" + username + "&page=" + (page + 1)
			: null;

		return PlanGroupResponseDTO.ReadAll.builder()
			.planShare(planShare)
			.nextUri(nextUri)
			.build();
	}

	@Override
	public PlanGroupResponseDTO.ReadAll readByScrap(Authentication authentication, int page) {
		return null;
	}

	@Override
	public ReplyResponseDTO.ReadAll readReplies(Authentication authentication, Long planGroupId, int page) {
		return null;
	}

	@Override
	public PlanGroupResponseDTO.ReadAll search(String keyword, String order, int page) {
		if (order == null || order.isBlank()) {
			order = "updatedAt"; // 기본값
		}

		Sort sort = Sort.by(order).descending();

		Pageable pageable = PageRequest.of(page, 10, sort);

		Slice<PlanGroup> planGroupPage;

		if (keyword != null && !keyword.isBlank()) {
			planGroupPage = planGroupRepository.findByKeyword(keyword, pageable);
		} else {
			planGroupPage = planGroupRepository.findAll(pageable); // 키워드가 없으면 전체 검색
		}

		List<PlanGroupResponseDTO.Info> planShare = planGroupPage.getContent().stream()
			.map(PlanGroupMapper::toInfo)
			.toList();

		String nextUri = planGroupPage.hasNext()
			? "/api/v1/plan-group/search?keyword=" + keyword + "&order=" + order + "&page=" + (page + 1)
			: null;

		return PlanGroupResponseDTO.ReadAll.builder()
			.planShare(planShare)
			.nextUri(nextUri)
			.build();
	}

	@Override
	public void createReply(Authentication authentication, Long id, PlanGroupRequestDTO.CreateReply groupCreateReply) {
		if (!authentication.isAuthenticated()) {
			throw PlanError.UNAUTHORIZED_CREATE_REPLY.getException();
		}

		PlanGroup planGroup = planGroupRepository.findByIdAndDeleted(id, false)
			.orElseThrow(PlanError.NOT_FOUND::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		Reply reply = replyService.create(groupCreateReply, member);

		planGroupReplyService.create(planGroup, reply);
	}

	@Override
	public PlanGroupResponseDTO.LikeStatus toggleLikes(Authentication authentication, Long planGroupId) {
		if (!authentication.isAuthenticated()) {
			throw PlanError.UNAUTHORIZED_TOGGLE_LIKES.getException();
		}

		PlanGroup planGroup = planGroupRepository.findByIdAndDeleted(planGroupId, false)
			.orElseThrow(PlanError.NOT_FOUND::getException);

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		String likesKey = PLAN_GROUP_LIKES_PREFIX_KEY + planGroupId;
		String unlikesKey = PLAN_GROUP_UNLIKES_PREFIX_KEY + planGroupId;
		boolean isLiked = getLiked(memberId, planGroupId);

		if (!isLiked) {
			redisTemplate.opsForSet().add(likesKey, memberId.toString());
			redisTemplate.opsForSet().remove(unlikesKey, memberId.toString());
		} else {
			redisTemplate.opsForSet().add(unlikesKey, memberId.toString());
			redisTemplate.opsForSet().remove(likesKey, memberId.toString());
		}

		return new PlanGroupResponseDTO.LikeStatus(!isLiked, getLikes(planGroupId));
	}

	@Override
	public boolean toggleScrap(Authentication authentication, Long planGroupId) {
		if (!authentication.isAuthenticated()) {
			throw PlanError.UNAUTHORIZED_TOGGLE_SCRAP.getException();
		}

		Long authorId = planGroupRepository.findByIdAndDeleted(planGroupId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException).getMember().getId();

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		if (authorId == memberId) {
			throw PostError.BAD_REQUEST_TOGGLE_SCRAP.getException();
		}

		String scrapKey = PLAN_GROUP_SCRAP_PREFIX_KEY + planGroupId;
		String unscrapKey = PLAN_GROUP_UNSCRAP_PREFIX_KEY + planGroupId;
		boolean isScraped = getScraped(memberId, planGroupId);

		if (!isScraped) {
			redisTemplate.opsForSet().add(scrapKey, memberId.toString());
			redisTemplate.opsForSet().remove(unscrapKey, memberId.toString());
		} else {
			redisTemplate.opsForSet().add(unscrapKey, memberId.toString());
			redisTemplate.opsForSet().remove(scrapKey, memberId.toString());
		}

		return !isScraped;
	}

	private boolean getLiked(Long memberId, Long planGroupId) {
		String likesKey = PLAN_GROUP_LIKES_PREFIX_KEY + planGroupId;
		String unlikesKey = PLAN_GROUP_UNLIKES_PREFIX_KEY + planGroupId;

		boolean isLiked = redisTemplate.opsForSet().isMember(likesKey, memberId.toString());
		boolean isUnliked = redisTemplate.opsForSet().isMember(unlikesKey, memberId.toString());

		if (!isLiked && !isUnliked) {
			return planGroupLikesRepository.existsByPlanGroupIdAndMemberId(planGroupId, memberId);
		}

		return isLiked && !isUnliked;
	}

	private Long getLikes(Long planGroupId) {
		String likesKey = PLAN_GROUP_LIKES_PREFIX_KEY + planGroupId;
		String unlikesKey = PLAN_GROUP_UNLIKES_PREFIX_KEY + planGroupId;

		Long cachedLikes = redisTemplate.opsForSet().size(likesKey);
		Long cachedUnlikes = redisTemplate.opsForSet().size(unlikesKey);
		Long dbLikes = planGroupRepository.findLikesById(planGroupId);

		return dbLikes + (cachedLikes != null ? cachedLikes : 0) - (cachedUnlikes != null ? cachedUnlikes : 0);
	}

	private boolean getScraped(Long memberId, Long planGroupId) {
		String scrapKey = PLAN_GROUP_SCRAP_PREFIX_KEY + planGroupId;
		String unscrapKey = PLAN_GROUP_UNSCRAP_PREFIX_KEY + planGroupId;

		boolean isScrap = redisTemplate.opsForSet().isMember(scrapKey, memberId.toString());
		boolean isUnscrap = redisTemplate.opsForSet().isMember(unscrapKey, memberId.toString());

		if (!isScrap && !isUnscrap) {
			return planGroupScrapRepository.existsByPlanGroupIdAndMemberId(planGroupId, memberId);
		}

		return isScrap && !isUnscrap;
	}

	private Long getViews(Long planGroupId) {
		String cachedViewsStr = (String)redisTemplate.opsForHash()
			.get(PLAN_GROUP_VIEWS_PREFIX_KEY, planGroupId.toString());
		Long cachedViews = cachedViewsStr != null ? Long.parseLong(cachedViewsStr) : 0L;
		Long dbViews = planGroupRepository.findViewsById(planGroupId);

		return dbViews + cachedViews;
	}

	private void incrementViews(Long planGroupId) {
		redisTemplate.opsForHash().increment(PLAN_GROUP_VIEWS_PREFIX_KEY, planGroupId.toString(), 1);
	}

	private Pageable getPageable(int size, Sort sort) {
		return PageRequest.of(0, size, sort);
	}
}
