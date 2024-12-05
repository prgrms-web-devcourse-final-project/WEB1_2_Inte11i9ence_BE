package com.prgrmsfinal.skypedia.post.service;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;

public interface PostReplyService {
	ReplyResponseDTO.ReadAll readAll(Authentication authentication, Long postId, Long lastReplyId);

	void create(Post post, Reply reply);

	Long getReplyCount(Long postId);
}
