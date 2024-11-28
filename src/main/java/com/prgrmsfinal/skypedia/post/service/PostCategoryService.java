package com.prgrmsfinal.skypedia.post.service;

import com.prgrmsfinal.skypedia.post.entity.PostCategory;

import java.util.Optional;

public interface PostCategoryService {
    Optional<PostCategory> getByName(String name);

    boolean existsByName(String name);
}
