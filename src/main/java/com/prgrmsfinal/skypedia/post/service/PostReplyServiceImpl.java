package com.prgrmsfinal.skypedia.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.notify.constant.NotifyType;
import com.prgrmsfinal.skypedia.notify.dto.NotifyRequestDTO;
import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.post.entity.PostReply;
import com.prgrmsfinal.skypedia.post.entity.key.PostReplyId;
import com.prgrmsfinal.skypedia.post.exception.PostError;
import com.prgrmsfinal.skypedia.post.repository.PostReplyRepository;
import com.prgrmsfinal.skypedia.reply.dto.ReplyRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import com.prgrmsfinal.skypedia.reply.service.ReplyService;
import com.prgrmsfinal.skypedia.reply.util.ReplyMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostReplyServiceImpl implements PostReplyService {
	private final PostReplyRepository postReplyRepository;

	private final ReplyService replyService;

	private final ApplicationEventPublisher eventPublisher;

	private final MemberService memberService;

	@Override
	public ReplyResponseDTO.ReadAll readAll(Authentication authentication, Long postId, int page) {
		Pageable pageable = PageRequest.of(page, 20, Sort.by("repliedAt").descending());
		Slice<Reply> result = postReplyRepository.findAllByPostId(postId, pageable);

		if (result == null || result.isEmpty()) {
			return null;
		}

		boolean isAuth = authentication != null && authentication.isAuthenticated();

		List<ReplyResponseDTO.Read> replies = result.stream()
			.map(reply -> ReplyMapper.toDTO(reply
				, isAuth ? replyService.getLiked(memberService.getAuthenticatedMember(authentication).getId()
					, reply.getId()) : false
				, replyService.getLikes(reply.getId())
			)).toList();

		if (!result.hasNext()) {
			return new ReplyResponseDTO.ReadAll(replies, null);
		}

		String nextUri = new StringBuilder()
			.append("/api/v1/post/").append(postId)
			.append("/reply?page=").append(page + 1)
			.toString();

		return new ReplyResponseDTO.ReadAll(replies, nextUri);
	}

	@Override
	public void create(Post post, ReplyRequestDTO.Create request) {
		Reply reply = replyService.create(request);

		postReplyRepository.save(PostReply.builder()
			.id(new PostReplyId(post.getId(), reply.getId()))
			.post(post)
			.reply(reply)
			.build());

		Reply parentReply = reply.getParentReply();

		if (parentReply != null && !parentReply.getMember().getId().equals(reply.getMember().getId())) {
			eventPublisher.publishEvent(new NotifyRequestDTO.User(
				parentReply.getMember(),
				reply.getMember().getName() + "님이 당신의 댓글에 대댓글을 달았습니다.",
				NotifyType.REPLY,
				"/api/v1/post/" + post.getId(),
				LocalDateTime.now()
			));
		} else {
			eventPublisher.publishEvent(new NotifyRequestDTO.User(
				post.getMember(),
				reply.getMember().getName() + "님이 당신의 글에 댓글을 달았습니다.",
				NotifyType.REPLY,
				"/api/v1/post/" + post.getId(),
				LocalDateTime.now()
			));
		}
	}

	@Override
	public Long getReplyCount(Long postId) {
		return postReplyRepository.countRepliesByPostId(postId);
	}
}
