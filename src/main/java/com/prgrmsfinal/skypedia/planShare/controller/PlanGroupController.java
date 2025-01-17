package com.prgrmsfinal.skypedia.planShare.controller;

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

import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupResponseDTO;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupRepository;
import com.prgrmsfinal.skypedia.planShare.service.PlanGroupService;
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

@Tag(name = "일정 공유 게시판 API 컨트롤러", description = "일정 공유 게시판과 관련된 REST API를 제공하는 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plan-group")
@Validated
public class PlanGroupController {
	private final PlanGroupService planGroupService;
	private final PlanGroupRepository planGroupRepository;

	@Operation(
		summary = "전체 일정 공유 게시물 조회",
		description = "전체 일정 공유 게시물을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시물을 성공적으로 조회했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시물이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "지역 카테고리가 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "standard",
			description = "정렬 기준",
			required = false,
			example = "updatedAt",
			schema = @Schema(type = "string")
		),
		@Parameter(
			name = "regionName",
			description = "지역 카테고리",
			required = false,
			example = "부산",
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
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.ReadAll readAll(Authentication authentication,
		@RequestParam(name = "standard", defaultValue = "updatedAt") String standard,
		@RequestParam(name = "regionName", required = false) String regionName,
		@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return planGroupService.readAll(authentication, standard, regionName, page);
	}

	@Operation(
		summary = "단일 일정 공유 게시물 조회",
		description = "ID와 일치하는 일정 공유 게시물을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시물을 성공적으로 조회했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시물이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "planGroupId",
			description = "조회할 게시물의 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@GetMapping("/{planGroupId}")
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.Read read(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long planGroupId) {
		return planGroupService.read(authentication, planGroupId);
	}

	@Operation(
		summary = "특정 회원의 공유 게시물 조회",
		description = "특정 회원의 일정 공유 게시물을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시물을 성공적으로 조회했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시물이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "username",
			description = "조회할 회원의 닉네임",
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
	@GetMapping("/{username}")
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.ReadAll readByMember(@PathVariable("username") String username,
		@RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		return planGroupService.readByMember(username, page);
	}

	@Operation(
		summary = "스크랩한 게시물 목록 조회",
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
	@GetMapping("/scrap")
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.ReadAll readByScrap(Authentication authentication
		, @RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		return planGroupService.readByScrap(authentication, page);
	}

	@Operation(
		summary = "게시물 댓글 조회",
		description = "ID와 일치하는 게시물의 댓글을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "댓글을 성공적으로 조회했습니다.",
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
			name = "planGroupId",
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
	@GetMapping("/{planGroupId}/reply")
	@ResponseStatus(HttpStatus.OK)
	public ReplyResponseDTO.ReadAll readReplies(Authentication authentication
		, @PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long planGroupId
		, @RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		return planGroupService.readReplies(authentication, planGroupId, page);
	}

	@Operation(
		summary = "일정 공유 게시물 등록",
		description = "일정 공유 게시물을 등록합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시물을 성공적으로 등록했습니다.",
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
				description = "게시물의 지역 카테고리가 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@PostMapping
	public ResponseEntity<?> create(
		Authentication authentication,
		@Valid @RequestBody PlanGroupRequestDTO.Create groupCreate) {
		planGroupService.create(authentication, groupCreate);

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("groupImage", groupCreate.getGroupImage()));
	}

	@Operation(
		summary = "댓글 등록 (일정 공유)",
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
			name = "planGroupId",
			description = "게시글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PostMapping("/{planGroupId}/reply")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void createReply(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long planGroupId,
		@Valid @RequestBody PlanGroupRequestDTO.CreateReply groupCreateReply) {
		planGroupService.createReply(authentication, planGroupId, groupCreateReply);
	}

	@Operation(
		summary = "좋아요 토글",
		description = "게시글에 대한 좋아요를 토글하고, 좋아요 여부와 좋아요 수를 반환합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "좋아요를 성공적으로 토글했습니다.",
				content = @Content(mediaType = "application/json")
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
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "planGroupId",
			description = "좋아요를 토글할 일정 공유 게시글의 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "integer", minimum = "1")
		)
	})
	@PostMapping("/{planGroupId}/likes")
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.LikeStatus toggleLikes(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long planGroupId) {
		return planGroupService.toggleLikes(authentication, planGroupId);
	}

	@Operation(
		summary = "일정 공유 게시물 스크랩 토글",
		description = "게시글에 대한 스크랩을 토글하고, 스크랩 여부를 반환합니다.",
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
	@Parameter(
		name = "planGroupId",
		description = "스크랩을 토글할 게시글의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer", minimum = "1")
	)
	@PostMapping("/{planGroupId}/scrap")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Boolean> toggleScrap(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long planGroupId) {
		return Map.of("scraped", planGroupService.toggleScrap(authentication, planGroupId));
	}

	@Operation(
		summary = "일정 공유 게시물 수정",
		description = "ID와 일치하는 일정 공유 게시물을 수정합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시물을 성공적으로 수정했습니다.",
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
				description = "게시물이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameter(
		name = "planGroupId",
		description = "수정할 게시물의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "long", minimum = "1")
	)
	@PutMapping("/{planGroupId}")
	public ResponseEntity<?> update(Authentication authentication,
		@PathVariable("planGroupId") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long planGroupId,
		@Valid @RequestBody PlanGroupRequestDTO.Update groupUpdate) {
		String groupImageUrl = planGroupService.update(authentication, planGroupId, groupUpdate);

		if (groupImageUrl == null || groupImageUrl.isBlank()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(Map.of("groupImage", groupImageUrl));
	}

	@Operation(
		summary = "일정 공유 게시물 삭제",
		description = "ID와 일치하는 일정 공유 게시물을 삭제합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시물을 삭제했습니다.",
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
				description = "삭제할 게시물이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameter(
		name = "planGroupId",
		description = "삭제할 게시물의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "long", minimum = "1")
	)
	@DeleteMapping("/{planGroupId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long planGroupId) {
		planGroupService.delete(authentication, planGroupId);
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
			name = "planGroupId",
			description = "게시글 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PatchMapping("/{planGroupId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void restore(Authentication authentication,
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long planGroupId) {
		planGroupService.restore(authentication, planGroupId);
	}

	@Operation(
		summary = "게시물 검색",
		description = "검색어를 포함하는 게시물을 조회합니다.",
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
			name = "keyword",
			description = "검색어",
			required = false,
			example = "게시글 제목",
			schema = @Schema(type = "string")
		),
		@Parameter(
			name = "standard",
			description = "검색 결과 정렬 기준",
			required = false,
			example = "title",
			schema = @Schema(type = "string")
		),
		@Parameter(
			name = "page",
			description = "페이지",
			required = false,
			example = "0",
			schema = @Schema(type = "integer")
		)
	})
	@GetMapping("/search")
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.ReadAll search(@RequestParam("keyword") String keyword,
		@RequestParam(defaultValue = "updatedAt") String standard,
		@RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		return planGroupService.search(keyword, standard, page);
	}
}
