package com.prgrmsfinal.skypedia.post.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

public interface PostService {
	PostResponseDTO.Read read(Authentication authentication, Long postId);

	PostResponseDTO.ReadAll readAll(String category, String cursor, Long lastId, String order);

	PostResponseDTO.ReadAll readAll(String username, Long lastId);

	PostResponseDTO.ReadAll readAll(Authentication authentication, Long lastId);

	PostResponseDTO.ReadAll search(String keyword, String target, String cursor, Long lastId);

	ReplyResponseDTO.ReadAll readReplies(Authentication authentication, Long postId, Long lastReplyId);

	List<String> create(Authentication authentication, PostRequestDTO.Create request);

	void createReply(Authentication authentication, Long postId, PostRequestDTO.CreateReply request);

	List<String> modify(Authentication authentication, Long postId, PostRequestDTO.Modify request);

	void delete(Authentication authentication, Long postId);

	void restore(Authentication authentication, Long postId);

	PostResponseDTO.ToggleLikes toggleLikes(Authentication authentication, Long postId);

	boolean toggleScrap(Authentication authentication, Long postId);
}
