package com.prgrmsfinal.skypedia.reply.controller;

import org.springframework.http.HttpStatus;
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

import com.prgrmsfinal.skypedia.reply.dto.ReplyRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.service.ReplyService;

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
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reply")
@Log4j2
@Validated
@Tag(name = "댓글 API 컨트롤러", description = "댓글과 관련된 REST API를 제공하는 컨트롤러입니다.")
public class ReplyController {
	private final ReplyService replyService;

	@Operation(
		summary = "대댓글 목록 조회",
		description = "부모댓글 ID로 대댓글들을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "댓글 조회에 성공했습니다",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "댓글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "parentId",
			description = "부모댓글 ID",
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
	@GetMapping("/{parentId}")
	@ResponseStatus(HttpStatus.OK)
	public ReplyResponseDTO.ReadAll readAll(Authentication authentication,
		@PathVariable("parentId") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long parentId,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page) {
		return replyService.readAll(authentication, parentId, page);
	}

	@Operation(
		summary = "댓글 좋아요 토글",
		description = "댓글에 좋아요를 토글하고, 좋아요 여부를 반환합니다.",
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
				description = "댓글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "replyId",
			description = "댓글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PostMapping("/{replyId}/likes")
	@ResponseStatus(HttpStatus.OK)
	public ReplyResponseDTO.LikeStatus toggleLikes(Authentication authentication,
		@PathVariable("replyId") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long replyId) {
		return replyService.toggleLikes(authentication, replyId);
	}

	@Operation(
		summary = "댓글 수정",
		description = "회원이 작성한 댓글을 수정합니다.",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "댓글을 성공적으로 수정했습니다.",
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
				description = "댓글 수정 권한이 없습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "댓글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "replyId",
			description = "댓글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PutMapping("/{replyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void modify(Authentication authentication,
		@PathVariable("replyId") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long replyId,
		@Valid @RequestBody ReplyRequestDTO.Modify request) {
		replyService.modify(authentication, replyId, request);
	}

	@Operation(
		summary = "댓글 삭제",
		description = "회원이 작성한 댓글을 삭제합니다.",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "댓글을 성공적으로 삭제했습니다.",
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
				description = "댓글 삭제 권한이 없습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "댓글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "replyId",
			description = "댓글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@DeleteMapping("/{replyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(Authentication authentication,
		@PathVariable("replyId") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long replyId) {
		replyService.delete(authentication, replyId);
	}

	@Operation(
		summary = "댓글 복구",
		description = "회원이 작성한 댓글을 복구합니다.",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "댓글을 성공적으로 복구했습니다.",
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
				description = "댓글 복구 권한이 없습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "댓글이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "replyId",
			description = "댓글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PatchMapping("/{replyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void restore(Authentication authentication,
		@PathVariable("replyId") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long replyId) {
		replyService.restore(authentication, replyId);
	}
}
