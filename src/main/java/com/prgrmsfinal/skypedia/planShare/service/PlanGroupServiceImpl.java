package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.mapper.MemberMapper;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroup;
import com.prgrmsfinal.skypedia.planShare.entity.Region;
import com.prgrmsfinal.skypedia.planShare.exception.PlanError;
import com.prgrmsfinal.skypedia.planShare.mapper.PlanGroupMapper;
import com.prgrmsfinal.skypedia.planShare.repository.PlanDetailRepository;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupLikesRepository;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupRepository;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupScrapRepository;
import com.prgrmsfinal.skypedia.post.exception.PostError;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import com.prgrmsfinal.skypedia.reply.service.ReplyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanGroupServiceImpl implements PlanGroupService {
	private final RedisTemplate<String, String> redisTemplate;
	private final PlanGroupRepository planGroupRepository;
	private final PlanGroupReplyService planGroupReplyService;
	private final MemberService memberService;
	private final RegionService regionService;
	private final ReplyService replyService;
	private final PlanGroupLikesRepository planGroupLikesRepository;
	private final PlanGroupScrapRepository planGroupScrapRepository;
	private final PlanDetailRepository planDetailRepository;

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
	public List<PlanGroupResponseDTO.Info> readAll(Authentication authentication,
		PlanGroupResponseDTO.ReadAll groupReadAll) {
		List<PlanGroup> planGroups = planGroupRepository.findAll();
		return planGroups.stream()
			.map(planGroup -> {
				MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(planGroup.getMember());
				return PlanGroupMapper.transDTO(planGroup, memberInfo,
					getViews(planGroup.getId(), planGroup.getViews()),
					getLikes(planGroup.getId(), planGroup.getLikes()), 0L, null);
			})
			.toList();
	}

	@Override
	public PlanGroupResponseDTO.Read read(Authentication authentication, Long id) {
		PlanGroup planGroup = planGroupRepository.findByIdAndDeleted(id, false)
			.orElseThrow(PlanError.NOT_FOUND::getException);

		incrementViews(id);

		MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(planGroup.getMember());

		PlanGroupResponseDTO.Statistics statics = PlanGroupResponseDTO.Statistics.builder()
			.views(getViews(id, planGroup.getViews()))
			.likes(getLikes(id, planGroup.getLikes()))
			.liked(isCurrentMemberLiked(authentication, planGroup))
			.scraped(isCurrentMemberScraped(authentication, planGroup))
			.build();

		ReplyResponseDTO.ReadAll replies = planGroupReplyService.readAll(authentication, id, 0L);

		return PlanGroupMapper.transDTO(planGroup, memberInfo, statics, null, replies);
	}

	@Override
	@Transactional
	public List<String> create(Authentication authentication, PlanGroupRequestDTO.Create groupCreate) {
		if (!authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_CREATE.getException();
		}

		Region region = regionService.findByRegionName(groupCreate.getRegionName())
			.orElseThrow(PlanError.NOT_FOUND::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		planGroupRepository.save(
			PlanGroup.builder()
				.title(groupCreate.getTitle())
				.region(region)
				.member(member)
				.build()
		);

		return null;
	}

	@Override
	public List<String> update(Authentication authentication, Long id, PlanGroupRequestDTO.Update groupUpdate) {
		PlanGroup planGroup = planGroupRepository.findByIdAndDeleted(id, false)
			.orElseThrow(PlanError.NOT_FOUND::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		if (!planGroup.getMember().getId().equals(member.getId())) {
			throw PostError.UNAUTHORIZED_MODIFY.getException();
		}

		planGroup.modify(groupUpdate.getGroupImage(), groupUpdate.getTitle());

		planGroupRepository.save(planGroup);

		return null;
	}

	@Override
	@Transactional
	public void delete(Authentication authentication, Long id) {
		PlanGroup planGroup = planGroupRepository.findById(id)
			.orElseThrow(PlanError.NOT_FOUND::getException);
		planGroupRepository.delete(planGroup);
	}

	@Override
	public PlanGroupResponseDTO.ReadAll readByRegion(String regionName, Long lastPlanGroupId) {
		if (!regionService.existsByRegionName(regionName)) {
			throw PostError.NOT_FOUND_CATEGORY.getException();
		}

		List<PlanGroup> planGroups = null;

		planGroups = planGroupRepository.findPlanGroupById(lastPlanGroupId, false, regionName,
			getPageable(10, Sort.by(Sort.Direction.DESC, "id")));

		if (planGroups == null || planGroups.isEmpty()) {
			throw PlanError.NOT_FOUND.getException();
		}

		List<PlanGroupResponseDTO.Info> response = planGroups.stream()
			.map(planGroup -> {
				MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(planGroup.getMember());
				Long replies = planGroupReplyService.getReplyCount(planGroup.getId());
				return PlanGroupMapper.transDTO(planGroup, memberInfo,
					getViews(planGroup.getId(), planGroup.getViews()),
					getLikes(planGroup.getId(), planGroup.getLikes()), replies, null);
			}).toList();

		PlanGroup lastPlanGroup = planGroups.get(planGroups.size() - 1);
		StringBuilder nextUri = new StringBuilder("/api/v1/posts?lastId=" + lastPlanGroup.getId());

		if (regionName != null) {
			nextUri.append("&regionName=" + regionName);
		}

		return new PlanGroupResponseDTO.ReadAll(response, nextUri.toString());
	}

	@Override
	public PlanGroupResponseDTO.ReadAll readByMember(String username, Long lastPlanGroupId) {

		if (!memberService.checkExistsByUsername(username)) {
			throw PostError.NOT_FOUND_USERNAME.getException();
		}

		List<PlanGroup> planGroups = planGroupRepository.findByUsername(username, lastPlanGroupId, false,
			getPageable(10, Sort.by(Sort.Direction.DESC, "id")));

		if (planGroups == null || planGroups.isEmpty()) {
			throw PostError.NOT_FOUND_POSTS.getException();
		}

		List<PlanGroupResponseDTO.Info> response = planGroups.stream()
			.map(planGroup -> {
				MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(planGroup.getMember());
				Long replies = planGroupReplyService.getReplyCount(planGroup.getId());
				return PlanGroupMapper.transDTO(planGroup, memberInfo,
					getViews(planGroup.getId(), planGroup.getViews()),
					getLikes(planGroup.getId(), planGroup.getLikes()), replies, null);
			}).toList();

		PlanGroup lastPost = planGroups.get(planGroups.size() - 1);
		String nextUri = new StringBuilder()
			.append("/api/v1/posts/").append(username)
			.append("?lastId=").append(lastPost.getId()).toString();

		return new PlanGroupResponseDTO.ReadAll(response, nextUri);
	}

	@Override
	public ReplyResponseDTO.ReadAll readReplies(Authentication authentication, Long id, Long lastReplyId) {
		PlanGroup planGroup = planGroupRepository.findByIdAndDeleted(id, false)
			.orElseThrow(PlanError.NOT_FOUND::getException);

		return planGroupReplyService.readAll(authentication, id, lastReplyId);
	}

	@Override
	public PlanGroupResponseDTO.ReadAll search(String keyword, String target, String cursor, Long lastPlanGroupId) {
		List<PlanGroupResponseDTO.Search> results = switch (target) {
			case "title" -> planGroupRepository.findPlanGroupByTitleKeyword(
				keyword, Double.parseDouble(cursor), lastPlanGroupId);
			case "content" -> planDetailRepository.findPlanGroupByContentKeyword(
				keyword, Double.parseDouble(cursor), lastPlanGroupId);
			default -> throw PlanError.BAD_REQUEST.getException();
		};

		if (results == null || results.isEmpty()) {
			throw PlanError.NOT_FOUND.getException();
		}

		List<PlanGroupResponseDTO.Info> response = results.stream()
			.map(planGroup -> {
				MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(planGroup.getMember());
				Long replies = planGroupReplyService.getReplyCount(planGroup.getId());
				return PlanGroupMapper.transDTO(planGroup, memberInfo,
					getViews(planGroup.getId(), planGroup.getViews()),
					getLikes(planGroup.getId(), planGroup.getLikes()), replies, null);
			}).toList();

		PlanGroupResponseDTO.Search lastResult = results.get(results.size() - 1);

		String nextUri = new StringBuilder()
			.append("/api/v1/posts?keyword=").append(keyword)
			.append("&target=").append(target)
			.append("&lastrev=").append(lastResult.getRelevance())
			.append("&lastidx=").append(lastResult.getId()).toString();

		return new PlanGroupResponseDTO.ReadAll(response, nextUri);
	}

	@Override
	public void createReply(Authentication authentication, Long id, PlanGroupRequestDTO.CreateReply groupCreateReply) {
		if (!authentication.isAuthenticated()) {
			throw PlanError.UNAUTHORIZED_CREATE_REPLY.getException();
		}

		PlanGroup planGroup = planGroupRepository.findByIdAndDeleted(id, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		Reply reply = replyService.create(groupCreateReply, member);

		planGroupReplyService.create(planGroup, reply);
	}

	@Override
	public PlanGroupResponseDTO.ToggleLikes toggleLikes(Authentication authentication, Long id) {
		if (!authentication.isAuthenticated()) {
			throw PlanError.UNAUTHORIZED_TOGGLE_LIKES.getException();
		}

		PlanGroup planGroup = planGroupRepository.findByIdAndDeleted(id, false)
			.orElseThrow(PlanError.NOT_FOUND::getException);

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		String likesKey = PLAN_GROUP_LIKES_PREFIX_KEY + id;
		String unlikesKey = PLAN_GROUP_UNLIKES_PREFIX_KEY + id;
		boolean isLiked = isCurrentMemberLiked(authentication, planGroup);

		if (!isLiked) {
			redisTemplate.opsForSet().add(likesKey, memberId.toString());
			redisTemplate.opsForSet().remove(unlikesKey, memberId.toString());
		} else {
			redisTemplate.opsForSet().add(unlikesKey, memberId.toString());
			redisTemplate.opsForSet().remove(likesKey, memberId.toString());
		}

		return new PlanGroupResponseDTO.ToggleLikes(!isLiked, getLikes(planGroup.getId(), planGroup.getLikes()));
	}

	@Override
	public boolean toggleScrap(Authentication authentication, Long id) {
		if (!authentication.isAuthenticated()) {
			throw PlanError.UNAUTHORIZED_TOGGLE_SCRAP.getException();
		}

		PlanGroup planGroup = planGroupRepository.findByIdAndDeleted(id, false)
			.orElseThrow(PlanError.NOT_FOUND::getException);

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		String scrapKey = PLAN_GROUP_SCRAP_PREFIX_KEY + id;
		String unscrapKey = PLAN_GROUP_UNSCRAP_PREFIX_KEY + id;
		boolean isScraped = isCurrentMemberScraped(authentication, planGroup);

		if (!isScraped) {
			redisTemplate.opsForSet().add(scrapKey, memberId.toString());
			redisTemplate.opsForSet().remove(unscrapKey, memberId.toString());
		} else {
			redisTemplate.opsForSet().add(unscrapKey, memberId.toString());
			redisTemplate.opsForSet().remove(scrapKey, memberId.toString());
		}

		return !isScraped;
	}

	private Long getLikes(Long id, Long likes) {
		String likesKey = StringUtils.join(PLAN_GROUP_LIKES_PREFIX_KEY, id);
		String unlikesKey = StringUtils.join(PLAN_GROUP_UNLIKES_PREFIX_KEY, id);

		if (Boolean.TRUE.equals(redisTemplate.hasKey(likesKey))) {
			likes += redisTemplate.opsForSet().size(likesKey);
		}

		if (Boolean.TRUE.equals(redisTemplate.hasKey(unlikesKey))) {
			likes -= redisTemplate.opsForSet().size(unlikesKey);
		}

		return likes;
	}

	private Long getViews(Long id, Long views) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(StringUtils.join(PLAN_GROUP_VIEWS_PREFIX_KEY, id)))) {
			views += (Long)redisTemplate.opsForHash().get(PLAN_GROUP_VIEWS_PREFIX_KEY, id);
		}

		return views;
	}

	private void incrementViews(Long id) {
		redisTemplate.opsForHash().increment(PLAN_GROUP_VIEWS_PREFIX_KEY, id.toString(), 1);
	}

	private boolean isCurrentMemberLiked(Authentication authentication, PlanGroup planGroup) {
		String likesKey = StringUtils.join(PLAN_GROUP_LIKES_PREFIX_KEY, planGroup.getId());
		String unlikesKey = StringUtils.join(PLAN_GROUP_UNLIKES_PREFIX_KEY, planGroup.getId());
		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likesKey, memberId))) {
			return true;
		}

		if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(unlikesKey, memberId))) {
			return false;
		}

		if (planGroupLikesRepository.existsByPlanGroupIdAndMemberId(planGroup.getId(), memberId)) {
			return true;
		}

		return false;
	}

	private boolean isCurrentMemberScraped(Authentication authentication, PlanGroup planGroup) {
		String scrapKey = PLAN_GROUP_SCRAP_PREFIX_KEY + planGroup.getId();
		String unscrapKey = PLAN_GROUP_UNLIKES_PREFIX_KEY + planGroup.getId();
		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(scrapKey, memberId))) {
			return true;
		}

		if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(unscrapKey, memberId))) {
			return false;
		}

		if (planGroupLikesRepository.existsByPlanGroupIdAndMemberId(planGroup.getId(), memberId)) {
			return true;
		}

		return false;
	}

	private Pageable getPageable(int size, Sort sort) {
		return PageRequest.of(0, size, sort);
	}
}
