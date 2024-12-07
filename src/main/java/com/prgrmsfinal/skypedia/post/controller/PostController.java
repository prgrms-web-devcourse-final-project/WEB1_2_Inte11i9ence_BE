package com.prgrmsfinal.skypedia.post.controller;

import java.util.List;
import java.util.Map;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import com.prgrmsfinal.skypedia.post.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@Tag(name = "게시글 API 컨트롤러", description = "게시글과 관련된 REST API를 제공하는 컨트롤러입니다.")
public class PostController {
	private final PostService postService;
	private final MemberService memberService;

	@Operation(
		summary = "게시글 단일 조회",
		description = "게시글의 세부내용을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시글 조회 성공",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않음",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "id",
		description = "조회할 게시글의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer", minimum = "1")
	)
	@GetMapping("/{postId}")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, PostResponseDTO.Read> read(Authentication authentication, @PathVariable Long postId) {
		Member currentMember = memberService.getAuthenticatedMember(authentication);
		return Map.of("result", postService.read((Authentication) currentMember, postId));
	}

	@Operation(
		summary = "게시글 목록 조회",
		description = "게시글의 목록을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시글 목록을 성공적으로 조회했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "204",
				description = "게시글 목록이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "잘못된 정렬 조건을 명시했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "해당 카테고리는 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "category",
			description = "게시글 카테고리",
			required = false,
			example = "0",
			schema = @Schema(type = "string")
		),
		@Parameter(
			name = "order",
			description = "게시글 정렬 조건",
			required = false,
			example = "title",
			schema = @Schema(type = "string")
		),
		@Parameter(
			name = "last",
			description = "마지막 인덱스",
			required = false,
			example = "0",
			schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
		)
	})
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Map<String, PostResponseDTO.ReadAll> readAll(@RequestParam("category") String category
		, @RequestParam("order") String order
		, @RequestParam(name = "last", defaultValue = "0") Long lastPostId) {
		return Map.of("result", postService.readAll(category, lastPostId, order));
	}

	@Operation(
		summary = "게시글 등록",
		description = "새로운 게시글 등록합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "새로운 게시글을 등록했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "401",
				description = "게시글 작성 권한이 없습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@PostMapping
	public ResponseEntity<?> create(Authentication authentication, PostRequestDTO.Create request) {
		List<String> uploadUrls = postService.create(authentication, request);

		if (uploadUrls.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uploadUrls", uploadUrls));
	}

	@Operation(
		summary = "좋아요 토글",
		description = "게시글에 대한 좋아요를 토글합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "좋아요를 성공적으로 토글했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "postId",
		description = "좋아요를 토글할 게시글의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer", minimum = "1")
	)
	@PostMapping("/{postId}/likes")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Boolean> toggleLikes(Authentication authentication, @PathVariable Long postId) {
		return Map.of("liked", postService.toggleLikes(authentication, postId));
	}

	@Operation(
		summary = "스크랩 토글",
		description = "게시글에 대한 스크랩을 토글합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "스크랩을 성공적으로 토글했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "postId",
		description = "스크랩을 토글할 게시글의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer", minimum = "1")
	)
	@PostMapping("/{postId}/scrap")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Boolean> toggleScrap(Authentication authentication, @PathVariable Long postId) {
		return Map.of("scraped", postService.toggleScrap(authentication, postId));
	}

	@Operation(
		summary = "게시글 수정",
		description = "해당 게시글을 수정합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "해당 게시글을 수정했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "401",
				description = "게시글 수정 권한이 없습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "postId",
		description = "스크랩을 토글할 게시글의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer", minimum = "1")
	)
	@PutMapping("/{postId}")
	public ResponseEntity<?> modify(Authentication authentication, @PathVariable Long postId,
		@RequestBody PostRequestDTO.Modify request) {
		List<String> uploadUrls = postService.modify(authentication, postId, request);

		if (uploadUrls.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(Map.of("uploadUrls", uploadUrls));
	}

	@Operation(
		summary = "게시글 삭제",
		description = "해당 게시글을 삭제합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "해당 게시글을 삭제했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "401",
				description = "게시글 삭제 권한이 없습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "postId",
		description = "삭제할 게시글의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer", minimum = "1")
	)
	@DeleteMapping("/{postId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(Authentication authentication, @PathVariable Long postId) {
		postService.delete(authentication, postId);
	}

	@Operation(
		summary = "게시글 복구",
		description = "해당 게시글을 복구합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "해당 게시글을 복구했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "401",
				description = "게시글 복구 권한이 없습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "postId",
		description = "복구할 게시글의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer", minimum = "1")
	)
	@PatchMapping("/{postId}/restore")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void restore(Authentication authentication, @PathVariable Long postId) {
		postService.restore(authentication, postId);
	}
}
