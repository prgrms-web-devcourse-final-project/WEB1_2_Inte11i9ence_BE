package com.prgrmsfinal.skypedia.post.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.notify.constant.NotifyType;
import com.prgrmsfinal.skypedia.notify.dto.NotifyRequestDTO;
import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.post.entity.PostReply;
import com.prgrmsfinal.skypedia.post.exception.PostError;
import com.prgrmsfinal.skypedia.post.repository.PostReplyRepository;
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

	@Override
	public ReplyResponseDTO.ReadAll readAll(Authentication authentication, Long postId, Long lastReplyId) {
		List<Reply> results = postReplyRepository.findRepliesByPostId(postId, lastReplyId,
			PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "repliedAt")));

		if (results == null || results.isEmpty()) {
			throw PostError.NOT_FOUND_REPLIES.getException();
		}

		List<ReplyResponseDTO.Read> response = results.stream()
			.map(
				result -> ReplyMapper.toDTO(result, replyService.isCurrentMemberLiked(authentication, result),
					replyService.getLikes(result)))
			.toList();

		Reply lastReply = results.get(results.size() - 1);

		String nextUri = new StringBuilder()
			.append("/api/v1/post/").append(postId)
			.append("/reply?lastidx=").append(lastReply.getId())
			.toString();

		return new ReplyResponseDTO.ReadAll(response, nextUri);
	}

	@Override
	public void create(Post post, Reply reply) {
		postReplyRepository.save(PostReply.builder()
			.post(post)
			.reply(reply)
			.build());

		Reply parentReply = reply.getParentReply();

		if (parentReply != null && parentReply.getMember().getId() != reply.getMember().getId()) {
			eventPublisher.publishEvent(NotifyRequestDTO.User.builder()
				.member(parentReply.getMember())
				.notifyType(NotifyType.REPLY)
				.content(reply.getMember().getName() + "님이 당신의 댓글에 대댓글을 달았습니다.")
				.uri("/api/v1/post/" + post.getId())
				.build());
		} else {
			eventPublisher.publishEvent(NotifyRequestDTO.User.builder()
				.member(post.getMember())
				.notifyType(NotifyType.REPLY)
				.content(reply.getMember().getName() + "님이 당신의 글에 댓글을 달았습니다.")
				.uri("/api/v1/post/" + post.getId())
				.build());
		}
	}

	@Override
	public Long getReplyCount(Long postId) {
		return postReplyRepository.countRepliesByPostId(postId);
	}
}
