package com.prgrmsfinal.skypedia.post.service;

import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PostService {
    PostResponseDTO.Read read(Authentication authentication, Long postId);

    List<PostResponseDTO.Read> readAll(String category, Long lastId, String order);

    void create(Authentication authentication, PostRequestDTO.Create request);

    void modify(Authentication authentication, Long postId, PostRequestDTO.Modify request);

    void delete(Authentication authentication, Long postId);

    void restore(Authentication authentication, Long postId);

    boolean toggleLikes(Authentication authentication, Long postId);
}
