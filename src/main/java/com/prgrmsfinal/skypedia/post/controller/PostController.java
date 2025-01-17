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
import jakarta.validation.constraints.NotNull;
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
		description = "게시글 ID로 게시글을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시글 조회에 성공했습니다",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "postId",
			description = "게시글의 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@GetMapping("/post/{postId}")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.Read read(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId) {
		return postService.read(authentication, postId);
	}

	@Operation(
		summary = "게시글 댓글 조회",
		description = "게시글 ID에 해당하는 댓글들을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "댓글들을 성공적으로 조회했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "postId",
			description = "게시글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		),
		@Parameter(
			name = "page",
			description = "페이지",
			required = false,
			example = "0",
			schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
		)
	})
	@GetMapping("/post/{postId}/reply")
	@ResponseStatus(HttpStatus.OK)
	public ReplyResponseDTO.ReadAll readReply(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId
		, @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return postService.readReplies(authentication, postId, page);
	}

	@Operation(
		summary = "게시글 목록 조회",
		description = "카테고리, 정렬 옵션을 기준으로 게시글 목록을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시글들을 성공적으로 조회했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "400",
				description = "잘못된 정렬 기준입니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "해당 게시글 카테고리가 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "order",
			description = "정렬 기준",
			required = false,
			example = "title",
			schema = @Schema(type = "string")
		),
		@Parameter(
			name = "category",
			description = "카테고리",
			required = false,
			example = "서울",
			schema = @Schema(type = "string")
		),
		@Parameter(
			name = "page",
			description = "페이지",
			required = false,
			example = "0",
			schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
		)
	})
	@GetMapping("/posts")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.ReadAll readAll(@RequestParam(name = "order", required = false) String order
		, @RequestParam(name = "category", required = false) String category
		, @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return postService.readAll(order, category, page);
	}

	@Operation(
		summary = "게시글 목록 조회 (회원)",
		description = "회원 닉네임을 기준으로 게시글 목록을 최신순으로 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시글들을 성공적으로 조회했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "username",
			description = "회원 닉네임",
			required = true,
			example = "username",
			schema = @Schema(type = "string")
		),
		@Parameter(
			name = "page",
			description = "페이지",
			required = false,
			example = "0",
			schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
		)
	})
	@GetMapping("/posts/{username}")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.ReadAll readAll(@PathVariable("username") String username
		, @RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		return postService.readAll(username, page);
	}

	@Operation(
		summary = "게시글 목록 조회 (스크랩)",
		description = "로그인 한 회원이 스크랩한 게시글 목록을 최신순으로 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시글들을 성공적으로 조회했습니다.",
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
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "page",
			description = "페이지",
			required = false,
			example = "0",
			schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
		)
	})
	@GetMapping("/posts/scrap")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.ReadAll readAll(Authentication authentication
		, @RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		return postService.readAll(authentication, page);
	}

	@Operation(
		summary = "게시글 검색",
		description = "키워드를 포함하는 게시글 목록을 정확도 순으로 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시글들을 성공적으로 조회했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "400",
				description = "잘못된 검색 기준입니다. (제목, 해쉬태그만 가능)",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "400",
				description = "검색 키워드는 2자 이상이어야 합니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "keyword",
			description = "검색 키워드",
			required = false,
			example = "게시글 제목",
			schema = @Schema(type = "string", minLength = 2, nullable = false)
		),
		@Parameter(
			name = "option",
			description = "검색 기준",
			required = false,
			example = "title",
			schema = @Schema(type = "string")
		),
		@Parameter(
			name = "page",
			description = "페이지",
			required = false,
			example = "0",
			schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
		)
	})
	@GetMapping("/posts/search")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.ReadAll search(
		@RequestParam(value = "keyword", defaultValue = "") @NotNull(message = "null 값은 허용되지 않습니다.") String keyword
		, @RequestParam(name = "option", required = false) String option
		, @RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		return postService.search(keyword, option, page);
	}

	@Operation(
		summary = "게시글 등록",
		description = "새로운 게시글을 등록합니다.",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "게시글을 성공적으로 등록했습니다.",
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
				responseCode = "404",
				description = "게시글 카테고리가 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	
	@PostMapping("/post")
	public ResponseEntity<?> create(Authentication authentication, @Valid @RequestBody PostRequestDTO.Create request) {
		List<String> uploadUrls = postService.create(authentication, request);

		if (uploadUrls == null || uploadUrls.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uploadUrls", uploadUrls));
	}

	@Operation(
		summary = "댓글 등록 (게시글)",
		description = "게시글에 새로운 댓글을 등록합니다.",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "댓글을 성공적으로 등록했습니다.",
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
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "postId",
			description = "게시글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PostMapping("/post/{postId}/reply")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void createReply(Authentication authentication,
		@PathVariable("postId") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId,
		@Valid @RequestBody PostRequestDTO.CreateReply request) {
		postService.createReply(authentication, postId, request);
	}

	@Operation(
		summary = "게시글 좋아요 토글",
		description = "게시글에 좋아요를 토글하고, 좋아요 여부와 좋아요 수를 반환합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "좋아요를 성공적으로 토글했습니다.",
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
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "postId",
			description = "게시글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PostMapping("/post/{postId}/likes")
	@ResponseStatus(HttpStatus.OK)
	public PostResponseDTO.LikeStatus toggleLikes(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId) {
		return postService.toggleLikes(authentication, postId);
	}

	@Operation(
		summary = "게시글 스크랩 토글",
		description = "게시글에 스크랩을 토글하고, 스크랩 여부를 반환합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "스크랩을 성공적으로 토글했습니다.",
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
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "postId",
			description = "게시글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PostMapping("/post/{postId}/scrap")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Boolean> toggleScrap(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId) {
		return Map.of("scraped", postService.toggleScrap(authentication, postId));
	}

	@Operation(
		summary = "게시글 수정",
		description = "회원이 작성한 게시글을 수정합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시글을 성공적으로 수정했습니다.",
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
				description = "게시글 수정 권한이 없습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "postId",
			description = "게시글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PutMapping("/post/{postId}")
	public ResponseEntity<?> modify(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId,
		@Valid @RequestBody PostRequestDTO.Modify request) {
		List<String> uploadUrls = postService.modify(authentication, postId, request);

		if (uploadUrls == null || uploadUrls.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(Map.of("uploadUrls", uploadUrls));
	}

	@Operation(
		summary = "게시글 삭제",
		description = "회원이 작성한 게시글을 삭제합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시글을 성공적으로 삭제했습니다.",
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
				description = "게시글 삭제 권한이 없습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "postId",
			description = "게시글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@DeleteMapping("/post/{postId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId) {
		postService.delete(authentication, postId);
	}

	@Operation(
		summary = "게시글 복구",
		description = "회원이 삭제한 게시글을 복구합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시글을 성공적으로 복구했습니다.",
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
				description = "게시글 복구 권한이 없습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "postId",
			description = "게시글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PatchMapping("/post/{postId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void restore(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long postId) {
		postService.restore(authentication, postId);
	}
}
