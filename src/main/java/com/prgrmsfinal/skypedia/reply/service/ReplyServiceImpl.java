package com.prgrmsfinal.skypedia.reply.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.Role;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import com.prgrmsfinal.skypedia.reply.exception.ReplyError;
import com.prgrmsfinal.skypedia.reply.repository.ReplyLikesRepository;
import com.prgrmsfinal.skypedia.reply.repository.ReplyRepository;
import com.prgrmsfinal.skypedia.reply.util.ReplyMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
	private final RedisTemplate<String, String> redisTemplate;

	private final ReplyRepository replyRepository;

	private final ReplyLikesRepository replyLikesRepository;

	private final MemberService memberService;

	@Value("${reply.likes.prefix.key}")
	private String REPLY_LIKES_PREFIX_KEY;

	@Value("${reply.unlikes.prefix.key}")
	private String REPLY_UNLIKES_PREFIX_KEY;

	@Override
	public ReplyResponseDTO.ReadAll readAll(Authentication authentication, Long parentId, int page) {
		Pageable pageable = PageRequest.of(page, 20, Sort.by("id").ascending());
		Slice<Reply> result = replyRepository.findAllByParentId(parentId, pageable);

		if (result == null || result.isEmpty()) {
			throw ReplyError.NOT_FOUND_REPLIES.getException();
		}

		boolean isAuth = authentication != null && authentication.isAuthenticated();

		List<ReplyResponseDTO.Read> replies = result.stream()
			.map(reply -> ReplyMapper.toDTO(reply
				, isAuth ? getLiked(memberService.getAuthenticatedMember(authentication).getId(), reply.getId()) : false
				, getLikes(reply.getId())
			)).toList();

		if (!result.hasNext()) {
			return new ReplyResponseDTO.ReadAll(replies, null);
		}

		String nextUri = new StringBuilder()
			.append("/api/v1/reply/").append(parentId)
			.append("?page=").append(page + 1)
			.toString();

		return new ReplyResponseDTO.ReadAll(replies, nextUri);
	}

	@Override
	public Reply create(ReplyRequestDTO.Create request) {
		Reply parentReply = null;

		if (request.getParentId() != null) {
			parentReply = replyRepository.findById(request.getParentId())
				.orElseThrow(ReplyError.NOT_FOUND_PARENT_REPLY::getException);
		}

		return replyRepository.save(Reply.builder()
			.parentReply(parentReply)
			.content(request.getContent())
			.member(request.getMember())
			.build());
	}

	@Override
	public void modify(Authentication authentication, Long replyId, ReplyRequestDTO.Modify request) {
		Reply reply = replyRepository.findByIdAndDeleted(replyId, false)
			.orElseThrow(ReplyError.NOT_FOUND_REPLY::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		if (!reply.getMember().getId().equals(member.getId())) {
			throw ReplyError.UNAUTHORIZED_MODIFY.getException();
		}

		reply.changeContent(request.getContent());

		replyRepository.save(reply);
	}

	@Override
	public void delete(Authentication authentication, Long replyId) {
		Reply reply = replyRepository.findByIdAndDeleted(replyId, false)
			.orElseThrow(ReplyError.NOT_FOUND_REPLY::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		if (!reply.getMember().getId().equals(member.getId())) {
			throw ReplyError.UNAUTHORIZED_DELETE.getException();
		}

		reply.delete();

		replyRepository.save(reply);
	}

	@Override
	public void restore(Authentication authentication, Long replyId) {
		Reply reply = replyRepository.findById(replyId)
			.orElseThrow(ReplyError.NOT_FOUND_RESTORE::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		if (!member.getRole().equals(Role.ROLE_ADMIN) && !member.getId().equals(reply.getMember().getId())) {
			throw ReplyError.UNAUTHORIZED_RESTORE.getException();
		}

		if (!reply.isDeleted()) {
			throw ReplyError.BAD_REQUEST_RESTORE.getException();
		}

		reply.restore();

		replyRepository.save(reply);
	}

	@Override
	public ReplyResponseDTO.LikeStatus toggleLikes(Authentication authentication, Long replyId) {
		if (!authentication.isAuthenticated()) {
			throw ReplyError.UNAUTHORIZED_TOGGLE_LIKES.getException();
		}

		Reply reply = replyRepository.findByIdAndDeleted(replyId, false)
			.orElseThrow(ReplyError.NOT_FOUND_REPLY::getException);

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		String likesKey = REPLY_LIKES_PREFIX_KEY + replyId;
		String unlikesKey = REPLY_UNLIKES_PREFIX_KEY + replyId;
		boolean isLiked = getLiked(memberId, replyId);

		if (!isLiked) {
			redisTemplate.opsForSet().add(likesKey, memberId.toString());
			redisTemplate.opsForSet().remove(unlikesKey, memberId.toString());
		} else {
			redisTemplate.opsForSet().add(unlikesKey, memberId.toString());
			redisTemplate.opsForSet().remove(likesKey, memberId.toString());
		}

		return new ReplyResponseDTO.LikeStatus(!isLiked, getLikes(reply.getId()));
	}

	@Override
	public boolean getLiked(Long memberId, Long replyId) {
		String likesKey = REPLY_LIKES_PREFIX_KEY + replyId;
		String unlikesKey = REPLY_UNLIKES_PREFIX_KEY + replyId;

		boolean isLiked = redisTemplate.opsForSet().isMember(likesKey, memberId.toString());
		boolean isUnliked = redisTemplate.opsForSet().isMember(unlikesKey, memberId.toString());

		if (!isLiked && !isUnliked) {
			return replyLikesRepository.existsByReplyIdAndMemberId(replyId, memberId);
		}

		return isLiked && !isUnliked;
	}

	@Override
	public Long getLikes(Long replyId) {
		String likesKey = REPLY_LIKES_PREFIX_KEY + replyId;
		String unlikesKey = REPLY_UNLIKES_PREFIX_KEY + replyId;

		Long cachedLikes = redisTemplate.opsForSet().size(likesKey);
		Long cachedUnlikes = redisTemplate.opsForSet().size(unlikesKey);
		Long dbLikes = replyRepository.findLikesById(replyId);

		return dbLikes + (cachedLikes != null ? cachedLikes : 0) - (cachedUnlikes != null ? cachedUnlikes : 0);
	}

	@Override
	public boolean isCurrentMemberLiked(Authentication authentication, Reply result) {
		return false;
	}

	@Override
	public Reply create(PlanGroupRequestDTO.CreateReply groupCreateReply, Member member) {
		return null;
	}
}
