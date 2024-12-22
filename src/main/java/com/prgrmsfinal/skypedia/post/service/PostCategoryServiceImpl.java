package com.prgrmsfinal.skypedia.post.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.post.dto.PostCategoryRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostCategoryResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.PostCategory;
import com.prgrmsfinal.skypedia.post.exception.PostError;
import com.prgrmsfinal.skypedia.post.repository.PostCategoryRepository;
import com.prgrmsfinal.skypedia.post.util.PostCategoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostCategoryServiceImpl implements PostCategoryService {
	private final PostCategoryRepository postCategoryRepository;

	@Override
	public PostCategoryResponseDTO.Read read(String name) {
		PostCategory postCategory = getByName(name)
			.orElseThrow(PostError.NOT_FOUND_CATEGORY::getException);

		return PostCategoryMapper.toDTO(postCategory);
	}

	@Override
	public List<PostCategoryResponseDTO.Read> readAll() {
		return postCategoryRepository.findAll().stream()
			.map(PostCategoryMapper::toDTO).toList();
	}

	@Override
	public void create(Authentication authentication, PostCategoryRequestDTO.Create request) {
		if (!authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_CREATE_CATEGORY.getException();
		}

		postCategoryRepository.save(PostCategoryMapper.toEntity(request));
	}

	@Override
	public Optional<PostCategory> getByName(String name) {
		return postCategoryRepository.findByName(name);
	}

	@Override
	public boolean existsByName(String name) {
		return postCategoryRepository.existsByName(name);
	}
}
