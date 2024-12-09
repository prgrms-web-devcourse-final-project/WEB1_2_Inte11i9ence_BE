package com.prgrmsfinal.skypedia.selectpost.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.selectpost.dto.SelectPostRequestDto;
import com.prgrmsfinal.skypedia.selectpost.dto.SelectPostResponseDto;
import com.prgrmsfinal.skypedia.selectpost.service.SelectPostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/select-post")
@RequiredArgsConstructor
public class SelectPostController {

	private final SelectPostService selectPostService;

	@GetMapping()
	public ResponseEntity<SelectPostResponseDto.ListResponse> getAllSelectPosts(
		@RequestParam(defaultValue = "10") int size
	) {
		SelectPostResponseDto.ListResponse response = selectPostService.readAllSelectPosts(size);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{selectPostId}")
	public ResponseEntity<SelectPostResponseDto> getSelectPost(
		@PathVariable Long selectPostId
	) {
		SelectPostResponseDto response = selectPostService.readSelectPost(selectPostId);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<List<PhotoResponseDTO.Info>> createSelectPost(
		@RequestBody SelectPostRequestDto requestDto,
		@RequestParam Long memberId
	) {
		List<PhotoResponseDTO.Info> response = selectPostService.createSelectPost(memberId, requestDto);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{selectPostId}")
	public ResponseEntity<SelectPostResponseDto> updateSelectPost(
		@PathVariable Long selectPostId,
		@RequestParam Long memberId,
		@RequestBody SelectPostRequestDto requestDto
	) {
		SelectPostResponseDto response = selectPostService.updateSelectPost(selectPostId, memberId, requestDto);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{selectPostId}")
	public ResponseEntity<Void> deleteSelectPost(
		@PathVariable Long selectPostId,
		@RequestParam Long memberId
	) {
		selectPostService.deleteSelectPost(selectPostId, memberId);
		return ResponseEntity.noContent().build();
	}
}