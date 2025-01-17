package com.prgrmsfinal.skypedia.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.member.dto.MemberRequestDTO;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.oauth2.jwt.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "회원 API 컨트롤러", description = "회원과 관련된 REST API를 제공하는 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Slf4j
@Validated
public class MemberController {
	private final MemberService memberService;
	private final MemberRepository memberRepository;

	/** 내 계정 조회 */
	@Operation(summary = "내 계정 조회", description = "현재 인증된 사용자의 계정 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = MemberResponseDTO.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping("/me")
	public ResponseEntity<MemberResponseDTO> getCurrentMember() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();

			Member member = memberRepository.findById(userDetails.getId())
				.orElseThrow(() -> new UsernameNotFoundException("Member not found"));
			return ResponseEntity.ok(new MemberResponseDTO(member));
		} catch (Exception e) {
			log.error("Error getting current member: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	/** 내 계정 수정 */
	@Operation(summary = "내 계정 수정", description = "현재 인증된 사용자의 계정 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = MemberResponseDTO.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PutMapping("/me")
	public ResponseEntity<MemberResponseDTO> putCurrentMember(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody MemberRequestDTO memberRequestDTO) {
		try {
			Member member = memberRepository.findById(userDetails.getId())
				.orElseThrow(() -> new UsernameNotFoundException("Member not found"));
			memberService.modify(member.getId(), memberRequestDTO);
			return ResponseEntity.ok(new MemberResponseDTO(member));
		} catch (Exception e) {
			log.error("Error updating member: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	/** 내 계정 탈퇴 */
	@Operation(summary = "내 계정 탈퇴", description = "현재 인증된 사용자의 계정을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204"),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteCurrentMember(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			memberService.deleteMember(userDetails.getId());
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			log.error("Error deleting member: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	/** 타인 계정 조회 */
	@Operation(summary = "타인 계정 조회", description = "특정 사용자의 계정 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = MemberResponseDTO.class))),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping("/{username}")
	public ResponseEntity<MemberResponseDTO> getMember(@PathVariable String username) {
		try {
			MemberResponseDTO response = memberService.readByUsername(username);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error getting member: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}