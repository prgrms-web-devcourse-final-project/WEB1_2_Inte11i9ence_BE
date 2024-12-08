package com.prgrmsfinal.skypedia.post.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.post.dto.PostCategoryRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostCategoryResponseDTO;
import com.prgrmsfinal.skypedia.post.service.PostCategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post-category")
@Validated
@Tag(name = "게시글 카테고리 API 컨트롤러", description = "게시글 카테고리와 관련된 REST API를 제공하는 컨트롤러입니다.")
public class PostCategoryController {
	private final PostCategoryService postCategoryService;

	@GetMapping("/{name}")
	public PostCategoryResponseDTO.Read read(@PathVariable("name") String name) {
		return postCategoryService.read(name);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<PostCategoryResponseDTO.Read> readAll() {
		return postCategoryService.readAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void create(Authentication authentication, @Valid @RequestBody PostCategoryRequestDTO.Create request) {
		postCategoryService.create(authentication, request);
	}
}
