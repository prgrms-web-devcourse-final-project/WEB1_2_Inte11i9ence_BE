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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

	@Operation(
		summary = "게시글 카테고리 단일 조회",
		description = "카테고리명으로 카테고리 상세 데이터를 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "해당 카테고리를 조회했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "해당 카테고리는 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@GetMapping("/{name}")
	@ResponseStatus(HttpStatus.OK)
	public PostCategoryResponseDTO.Read read(@PathVariable("name") String name) {
		return postCategoryService.read(name);
	}

	@Operation(
		summary = "게시글 카테고리 목록 조회",
		description = "전체 게시글 카테고리의 목록을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "전체 게시글을 성공적으로 조회했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "카테고리가 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<PostCategoryResponseDTO.Read> readAll() {
		return postCategoryService.readAll();
	}

	@Operation(
		summary = "게시글 카테고리 등록",
		description = "새로운 게시글 카테고리를 등록합니다.",
		responses = {
			@ApiResponse(
				responseCode = "201",
				description = "새로운 게시글 카테고리를 등록했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "401",
				description = "회원이 인증되지 않았습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "403",
				description = "게시글 등록 권한이 없습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void create(Authentication authentication, @Valid @RequestBody PostCategoryRequestDTO.Create request) {
		postCategoryService.create(authentication, request);
	}
}
