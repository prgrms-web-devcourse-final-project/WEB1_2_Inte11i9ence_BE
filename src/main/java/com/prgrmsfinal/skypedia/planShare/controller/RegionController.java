package com.prgrmsfinal.skypedia.planShare.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.planShare.dto.RegionDTO;
import com.prgrmsfinal.skypedia.planShare.service.RegionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "지역 카테고리 API 컨트롤러", description = "지역 카테고리와 관련된 REST API를 제공하는 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/region")
public class RegionController {
	private final RegionService regionService;

	@Operation(
		summary = "전체 지역 카테고리 조회",
		description = "전체 지역 카테고리를 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "지역 카테고리를 성공적으로 조회했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "지역 카테고리가 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<RegionDTO> readAll() {
		return regionService.readAll();
	}

	@Operation(
		summary = "지역 카테고리 등록",
		description = "지역 카테고리를 등록합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "지역 카테고리를 성공적으로 등록했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "지역 카테고리 등록에 실패했습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@PostMapping
	public ResponseEntity<RegionDTO> create(@Valid @RequestBody RegionDTO regionDTO) {
		RegionDTO createdRegion = regionService.register(regionDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdRegion);
	}

	@Operation(
		summary = "지역 카테고리 수정",
		description = "ID와 일치하는 지역 카테고리를 수정합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "지역 카테고리를 성공적으로 수정했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "지역 카테고리 수정에 실패했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "지역 카테고리가 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "id",
		description = "수정할 지역 카테고리의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer")
	)
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public RegionDTO update(@PathVariable @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long id,
		@Valid @RequestBody RegionDTO regionDTO) {
		regionDTO.setId(id);
		return regionService.update(regionDTO);
	}

	@Operation(
		summary = "지역 카테고리 삭제",
		description = "ID와 일치하는 지역 카테고리를 삭제합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "지역 카테고리를 삭제했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "400",
				description = "지역 카테고리 삭제에 실패했습니다.",
				content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
				responseCode = "404",
				description = "삭제할 지역 카테고리가 존재하지 않습니다.",
				content = @Content(mediaType = "application/json")
			)
		}
	)
	@Parameter(
		name = "id",
		description = "삭제할 지역 카테고리의 ID",
		required = true,
		example = "1",
		schema = @Schema(type = "integer")
	)
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, String> delete(@PathVariable("id") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long id) {
		regionService.delete(id);
		return Map.of("message", "삭제 완료");
	}
}
