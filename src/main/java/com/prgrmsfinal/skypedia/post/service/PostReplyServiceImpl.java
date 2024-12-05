package com.prgrmsfinal.skypedia.post.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.post.entity.PostReply;
import com.prgrmsfinal.skypedia.post.exception.PostError;
import com.prgrmsfinal.skypedia.post.repository.PostReplyRepository;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import com.prgrmsfinal.skypedia.reply.mapper.ReplyMapper;
import com.prgrmsfinal.skypedia.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostReplyServiceImpl implements PostReplyService {
	private final PostReplyRepository postReplyRepository;

	private final ReplyService replyService;

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
	}

	@Override
	public Long getReplyCount(Long postId) {
		return postReplyRepository.countRepliesByPostId(postId);
	}
}
