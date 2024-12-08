package com.prgrmsfinal.skypedia.reply.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
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
	public ReplyResponseDTO.ReadAll readAll(Authentication authentication, Long parentId, Long lastReplyId) {
		List<Reply> replies = replyRepository.findRepliesByParentId(parentId, lastReplyId, PageRequest.of(0, 20,
			Sort.by(Sort.Direction.ASC, "id")));

		if (replies == null || replies.isEmpty()) {
			throw ReplyError.NOT_FOUND_REPLIES.getException();
		}

		List<ReplyResponseDTO.Read> response = replies.stream()
			.map(reply -> ReplyMapper.toDTO(reply, isCurrentMemberLiked(authentication, reply), getLikes(reply)))
			.toList();

		Reply lastReply = replies.get(replies.size() - 1);

		String nextUri = new StringBuilder()
			.append("/api/v1/reply/").append(parentId)
			.append("?lastidx=").append(lastReply.getId())
			.toString();

		return new ReplyResponseDTO.ReadAll(response, nextUri);
	}

	@Override
	public Reply create(PostRequestDTO.CreateReply request, Member member) {
		Reply parentReply = null;

		if (request.getParentId() != null) {
			parentReply = replyRepository.findById(request.getParentId())
				.orElseThrow(ReplyError.NOT_FOUND_PARENT_REPLY::getException);
		}

		return replyRepository.save(Reply.builder()
			.parentReply(parentReply)
			.content(request.getContent())
			.build());
	}

	@Override
	public Reply create(PlanGroupRequestDTO.CreateReply groupCreateReply, Member member) {
		Reply parentReply = null;

		if (groupCreateReply.getParentId() != null) {
			parentReply = replyRepository.findById(groupCreateReply.getParentId())
				.orElseThrow(ReplyError.NOT_FOUND_PARENT_REPLY::getException);
		}

		return replyRepository.save(Reply.builder()
			.parentReply(parentReply)
			.content(groupCreateReply.getContent())
			.build());
	}

	@Override
	public void modify(Authentication authentication, Long replyId, ReplyRequestDTO.Modify request) {
		Reply reply = replyRepository.findByIdAndDeleted(replyId, false)
			.orElseThrow(ReplyError.NOT_FOUND_REPLY::getException);

		if (reply.getMember().getId() != memberService.getAuthenticatedMember(authentication).getId()) {
			throw ReplyError.UNAUTHORIZED_MODIFY.getException();
		}

		reply.changeContent(request.getContent());

		replyRepository.save(reply);
	}

	@Override
	public void delete(Authentication authentication, Long replyId) {
		Reply reply = replyRepository.findByIdAndDeleted(replyId, false)
			.orElseThrow(ReplyError.NOT_FOUND_REPLY::getException);

		if (reply.getMember().getId() != memberService.getAuthenticatedMember(authentication).getId()) {
			throw ReplyError.UNAUTHORIZED_DELETE.getException();
		}

		reply.delete();

		replyRepository.save(reply);
	}

	@Override
	public void restore(Authentication authentication, Long replyId) {
		Reply reply = replyRepository.findById(replyId)
			.orElseThrow(ReplyError.NOT_FOUND_RESTORE::getException);

		if (reply.getMember().getId() != memberService.getAuthenticatedMember(authentication).getId()) {
			throw ReplyError.UNAUTHORIZED_RESTORE.getException();
		}

		if (!reply.isDeleted()) {
			throw ReplyError.BAD_REQUEST_RESTORE.getException();
		}

		reply.restore();

		replyRepository.save(reply);
	}

	@Override
	public ReplyResponseDTO.ToggleLikes toggleLikes(Authentication authentication, Long replyId) {
		if (!authentication.isAuthenticated()) {
			throw ReplyError.UNAUTHORIZED_TOGGLE_LIKES.getException();
		}

		Reply reply = replyRepository.findByIdAndDeleted(replyId, false)
			.orElseThrow(ReplyError.NOT_FOUND_REPLY::getException);

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		String likesKey = REPLY_LIKES_PREFIX_KEY + replyId;
		String unlikesKey = REPLY_UNLIKES_PREFIX_KEY + replyId;
		boolean isLiked = isCurrentMemberLiked(authentication, reply);

		if (!isLiked) {
			redisTemplate.opsForSet().add(likesKey, memberId.toString());
			redisTemplate.opsForSet().remove(unlikesKey, memberId.toString());
		} else {
			redisTemplate.opsForSet().add(unlikesKey, memberId.toString());
			redisTemplate.opsForSet().remove(likesKey, memberId.toString());
		}

		return new ReplyResponseDTO.ToggleLikes(!isLiked, getLikes(reply));
	}

	@Override
	public Long getLikes(Reply reply) {
		String likesKey = REPLY_LIKES_PREFIX_KEY + reply.getId();
		String unlikesKey = REPLY_UNLIKES_PREFIX_KEY + reply.getId();
		Long likes = reply.getLikes();

		if (redisTemplate.hasKey(likesKey)) {
			likes += redisTemplate.opsForSet().size(likesKey);
		}

		if (redisTemplate.hasKey(unlikesKey)) {
			likes -= redisTemplate.opsForSet().size(unlikesKey);
		}

		return likes;
	}

	@Override
	public boolean isCurrentMemberLiked(Authentication authentication, Reply reply) {
		String likesKey = REPLY_LIKES_PREFIX_KEY + reply.getId();
		String unlikesKey = REPLY_UNLIKES_PREFIX_KEY + reply.getId();
		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likesKey, memberId))) {
			return true;
		}

		if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(unlikesKey, memberId))) {
			return false;
		}

		if (replyLikesRepository.existsByReplyIdAndMemberId(reply.getId(), memberId)) {
			return true;
		}

		return false;
	}
}
