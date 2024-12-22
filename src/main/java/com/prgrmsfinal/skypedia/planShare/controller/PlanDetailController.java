package com.prgrmsfinal.skypedia.planShare.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailResponseDTO;
import com.prgrmsfinal.skypedia.planShare.service.PlanDetailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "일정 공유 게시물 세부 일정 API 컨트롤러", description = "일정 공유의 세부 일정과 관련된 REST API를 제공하는 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plan-detail")
@Validated
public class PlanDetailController {
	private final PlanDetailService planDetailService;

	@Operation(
		summary = "전체 세부 일정 조회",
		description = "전체 세부 일정을 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "세부 일정을 성공적으로 조회했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "세부 일정이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<PlanDetailResponseDTO.ReadAll> readAll(
		@Valid @RequestBody PlanDetailResponseDTO.ReadAll detailReadAll) {
		return planDetailService.readAll(detailReadAll);
	}

	@Operation(
		summary = "일정 공유 게시물의 세부 일정 등록",
		description = "세부 일정을 등록합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "세부 일정을 성공적으로 등록했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "세부 일정 등록에 실패했습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@PostMapping
	public ResponseEntity<PlanDetailRequestDTO.Create> create(
		@Valid @RequestBody PlanDetailRequestDTO.Create detailCreate) {
		PlanDetailRequestDTO.Create createdPlanDetail = planDetailService.register(detailCreate);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdPlanDetail);
	}

	@Operation(
		summary = "세부 일정 수정",
		description = "ID와 일치하는 세부 일정을 수정합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "세부 일정을 성공적으로 수정했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "세부 일정 수정에 실패했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "세부 일정이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "id",
		description = "수정할 세부 일정의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer")
	)
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public PlanDetailRequestDTO.Update update(
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long id,
		@Valid @RequestBody PlanDetailRequestDTO.Update detailUpdate) {
		PlanDetailRequestDTO.Update locationImageUrl = planDetailService.update(id, detailUpdate);
		return locationImageUrl;
	}

	@Operation(
		summary = "세부 일정 삭제",
		description = "ID와 일치하는 세부 일정을 삭제합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "세부 일정을 삭제했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "세부 일정 삭제에 실패했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "삭제할 세부 일정이 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "id",
		description = "삭제할 세부 일정의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer")
	)
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Map<String, String>> delete(
		@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long id) {
		planDetailService.delete(id);
		return ResponseEntity.ok(Map.of("message", "삭제 완료"));
	}
}
