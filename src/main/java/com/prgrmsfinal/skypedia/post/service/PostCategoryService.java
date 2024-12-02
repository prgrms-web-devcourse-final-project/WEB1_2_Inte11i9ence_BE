package com.prgrmsfinal.skypedia.post.service;

import java.util.Optional;

import com.prgrmsfinal.skypedia.post.entity.PostCategory;

public interface PostCategoryService {
	Optional<PostCategory> getByName(String name);

	boolean existsByName(String name);
}
