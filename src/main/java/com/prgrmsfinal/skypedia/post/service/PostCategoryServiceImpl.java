package com.prgrmsfinal.skypedia.post.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.post.entity.PostCategory;
import com.prgrmsfinal.skypedia.post.repository.PostCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostCategoryServiceImpl implements PostCategoryService {
	private final PostCategoryRepository postCategoryRepository;

	@Override
	public Optional<PostCategory> getByName(String name) {
		return postCategoryRepository.findByName(name);
	}

	@Override
	public boolean existsByName(String name) {
		return postCategoryRepository.existsByName(name);
	}
}
