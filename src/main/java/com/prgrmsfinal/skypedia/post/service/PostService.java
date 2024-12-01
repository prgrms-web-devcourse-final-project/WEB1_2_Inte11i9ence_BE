package com.prgrmsfinal.skypedia.post.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;

public interface PostService {
	PostResponseDTO.Read read(Authentication authentication, Long postId);

	PostResponseDTO.ReadAll readAll(String category, Long lastId, String order);

	List<String> create(Authentication authentication, PostRequestDTO.Create request);

	List<String> modify(Authentication authentication, Long postId, PostRequestDTO.Modify request);

	void delete(Authentication authentication, Long postId);

	void restore(Authentication authentication, Long postId);

	boolean toggleLikes(Authentication authentication, Long postId);

	boolean toggleScrap(Authentication authentication, Long postId);
}
