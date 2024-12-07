package com.prgrmsfinal.skypedia.planShare.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "일정 공유 게시판 API 컨트롤러", description = "일정 공유 게시판과 관련된 REST API를 제공하는 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plan-group")
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
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시물이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<PlanGroupResponseDTO.Info> readAll(Authentication authentication,
		PlanGroupResponseDTO.ReadAll groupReadALL) {
		return planGroupService.readAll(authentication, groupReadALL);
	}

	@Operation(
		summary = "단일 일정 공유 게시물 조회",
		description = "ID와 일치하는 일정 공유 게시물을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시물을 성공적으로 조회했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시물이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "id",
		description = "조회할 게시물의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer")
	)
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.Read read(Authentication authentication, @PathVariable("id") Long id) {
		return planGroupService.read(authentication, id);
	}

	@GetMapping("/{id}/reply")
	@ResponseStatus(HttpStatus.OK)
	public ReplyResponseDTO.ReadAll readReplies(Authentication authentication, @PathVariable Long planGroupId
		, @RequestParam(name = "lastidx", defaultValue = "0") Long lastId) {
		return planGroupService.readReplies(authentication, planGroupId, lastId);
	}

	@Operation(
		summary = "일정 공유 게시물 등록",
		description = "일정 공유 게시물을 등록합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시물을 성공적으로 등록했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "게시물 등록에 실패했습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@PostMapping
	public ResponseEntity<?> create(
		Authentication authentication,
		@RequestBody PlanGroupRequestDTO.Create groupCreate) {
		List<String> thumbnailUrl = planGroupService.create(authentication, groupCreate);

		if (thumbnailUrl.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/post/{postId}/reply")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void createReply(Authentication authentication, @PathVariable("planGroupId") Long planGroupId,
		PlanGroupRequestDTO.CreateReply groupCreateReply) {
		planGroupService.createReply(authentication, planGroupId, groupCreateReply);
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
	@PostMapping("/{id}/likes")
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.ToggleLikes toggleLikes(Authentication authentication, @PathVariable Long planGroupId) {
		return planGroupService.toggleLikes(authentication, planGroupId);
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
	@PostMapping("/{id}/scrap")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Boolean> toggleScrap(Authentication authentication, @PathVariable Long planGroupId) {
		return Map.of("scraped", planGroupService.toggleScrap(authentication, planGroupId));
	}

	@Operation(
		summary = "일정 공유 게시물 수정",
		description = "ID와 일치하는 일정 공유 게시물을 수정합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시물을 성공적으로 수정했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "게시물 수정에 실패했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "게시물이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "id",
		description = "수정할 게시물의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer")
	)
	@PutMapping("/{id}")
	public ResponseEntity<?> update(Authentication authentication, @PathVariable("id") Long id,
		@RequestBody PlanGroupRequestDTO.Update groupUpdate) {
		List<String> groupImageUrl = planGroupService.update(authentication, id, groupUpdate);

		if (groupImageUrl.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(
			(PlanGroupRequestDTO.Update)Map.of("groupImageUrl", groupImageUrl));
	}

	@Operation(
		summary = "일정 공유 게시물 삭제",
		description = "ID와 일치하는 일정 공유 게시물을 삭제합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "게시물을 삭제했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "게시물 삭제에 실패했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "삭제할 게시물이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "id",
		description = "삭제할 게시물의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer")
	)
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(Authentication authentication, @PathVariable("id") Long id) {
		planGroupService.delete(authentication, id);
	}

	// 지역별 게시물 조회
	@GetMapping("/{regionName}")
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.ReadAll readByRegion(
		@PathVariable("regionName") String regionName,
		@RequestParam(name = "lastidx", defaultValue = "0") Long lastPlanGroupId) {
		return planGroupService.readByRegion(regionName, lastPlanGroupId);
	}

	// 회원별 게시물 조회
	@GetMapping("/{username}")
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.ReadAll readByMember(Authentication authentication,
		@PathVariable("username") String username,
		@RequestParam(name = "lastidx", defaultValue = "0") Long lastPlanGroupId) {
		return planGroupService.readByMember(username, lastPlanGroupId);
	}

	// 검색 기능
	@GetMapping("/search")
	@ResponseStatus(HttpStatus.OK)
	public PlanGroupResponseDTO.ReadAll search(@RequestParam("keyword") String keyword,
		@RequestParam("target") String target,
		@RequestParam("lastrev") String cursor,
		@RequestParam(name = "lastidx", defaultValue = "0") Long lastPlanGroupId) {
		return planGroupService.search(keyword, target, cursor, lastPlanGroupId);
	}
}
