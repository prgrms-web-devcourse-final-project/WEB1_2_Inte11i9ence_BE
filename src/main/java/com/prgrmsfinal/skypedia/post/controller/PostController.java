package com.prgrmsfinal.skypedia.post.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
@Tag(name = "게시글 API 컨트롤러", description = "게시글과 관련된 REST API를 제공하는 컨트롤러입니다.")
public class PostController {
	private final PostService postService;

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
	@GetMapping("/post/{postId}")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.Read read(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId) {
		return postService.read(authentication, postId);
	}

	@Operation(
		summary = "게시글 댓글 목록 조회",
		description = "게시글의 댓글 목록을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "댓글 목록 조회 성공",
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
		name = "lastidx",
		description = "기준이 될 마지막 인덱스",
		required = false,
		example = "0",
		schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
	)
	@GetMapping("/post/{postId}/reply")
	@ResponseStatus(HttpStatus.OK)
	public ReplyResponseDTO.ReadAll readReplies(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId
		, @RequestParam(name = "lastidx", defaultValue = "0", required = false) Long lastId) {
		return postService.readReplies(authentication, postId, lastId);
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
	@GetMapping("/posts")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.ReadAll readAll(@RequestParam("category") String category
		, @RequestParam(name = "order", required = false) String order
		, @RequestParam(name = "cursor", required = false) String cursor
		, @RequestParam(name = "lastidx", defaultValue = "0", required = false) Long lastPostId) {
		return postService.readAll(category, cursor, lastPostId, order);
	}

	@GetMapping("/posts/{username}")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.ReadAll readAll(@PathVariable("username") String username
		, @RequestParam(name = "lastidx", defaultValue = "0", required = false) Long lastPostId) {
		return postService.readAll(username, lastPostId);
	}

	@GetMapping("/posts/scrap")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.ReadAll readAll(Authentication authentication, @RequestParam("lastidx") Long lastId) {
		return postService.readAll(authentication, lastId);
	}

	@GetMapping("/posts/search")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.ReadAll search(@RequestParam("keyword") String keyword
		, @RequestParam(name = "target", required = false) String target
		, @RequestParam(name = "lastrev", required = false) String cursor
		, @RequestParam(name = "lastidx", required = false, defaultValue = "0") Long lastPostId) {
		return postService.search(keyword, target, cursor, lastPostId);
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
	@PostMapping("/post")
	public ResponseEntity<?> create(Authentication authentication, @Valid @RequestBody PostRequestDTO.Create request) {
		List<String> uploadUrls = postService.create(authentication, request);

		if (uploadUrls.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uploadUrls", uploadUrls));
	}

	@PostMapping("/post/{postId}/reply")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void createReply(Authentication authentication,
		@PathVariable("postId") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId,
		@Valid @RequestBody PostRequestDTO.CreateReply request) {
		postService.createReply(authentication, postId, request);
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
	@PostMapping("/post/{postId}/likes")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.ToggleLikes toggleLikes(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId) {
		return postService.toggleLikes(authentication, postId);
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
	@PostMapping("/post/{postId}/scrap")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Boolean> toggleScrap(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId) {
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
	@PutMapping("/post/{postId}")
	public ResponseEntity<?> modify(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId,
		@Valid @RequestBody PostRequestDTO.Modify request) {
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
	@DeleteMapping("/post/{postId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId) {
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
	@PatchMapping("/post/{postId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void restore(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId) {
		postService.restore(authentication, postId);
	}
}
