package com.prgrmsfinal.skypedia.post.service;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.reply.dto.ReplyRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;

public interface PostReplyService {
	ReplyResponseDTO.ReadAll readAll(Authentication authentication, Long postId, int page);

	void create(Post post, ReplyRequestDTO.Create request);

	Long getReplyCount(Long postId);
}
