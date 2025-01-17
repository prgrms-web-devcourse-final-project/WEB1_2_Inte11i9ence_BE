package com.prgrmsfinal.skypedia.post.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

public interface PostService {
	PostResponseDTO.Read read(Authentication authentication, Long postId);

	PostResponseDTO.ReadAll readAll(String order, String category, int page);

	PostResponseDTO.ReadAll readAll(String username, int page);

	PostResponseDTO.ReadAll readAll(Authentication authentication, int page);

	PostResponseDTO.ReadAll search(String keyword, String option, int page);

	ReplyResponseDTO.ReadAll readReplies(Authentication authentication, Long postId, int page);

	List<String> create(Authentication authentication, PostRequestDTO.Create request);

	void createReply(Authentication authentication, Long postId, PostRequestDTO.CreateReply request);

	List<String> modify(Authentication authentication, Long postId, PostRequestDTO.Modify request);

	void delete(Authentication authentication, Long postId);

	void restore(Authentication authentication, Long postId);

	PostResponseDTO.LikeStatus toggleLikes(Authentication authentication, Long postId);

	boolean toggleScrap(Authentication authentication, Long postId);
}
