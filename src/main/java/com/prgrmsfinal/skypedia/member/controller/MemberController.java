package com.prgrmsfinal.skypedia.member.controller;

import com.prgrmsfinal.skypedia.member.dto.MemberRequestDTO;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 API 컨트롤러", description = "회원과 관련된 REST API를 제공하는 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

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
    @ResponseStatus(HttpStatus.OK)
    public MemberResponseDTO getCurrentMember(Authentication authentication) {
        Member member = memberService.getAuthenticatedMember(authentication);
        return memberService.read(member.getId());
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
    @ResponseStatus(HttpStatus.OK)
    public MemberResponseDTO putCurrentMember(
            Authentication authentication,
            @Valid @RequestBody MemberRequestDTO memberRequestDTO) {
        Member member = memberService.getAuthenticatedMember(authentication);
        memberService.modify(member.getId(), memberRequestDTO);

        return new MemberResponseDTO(member);
    }

    /** 내 계정 탈퇴 */
    @Operation(summary = "내 계정 탈퇴", description = "현재 인증된 사용자의 계정을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentMember(Authentication authentication) {
        Member member = memberService.getAuthenticatedMember(authentication);
        memberService.deleteMember(member.getId());
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
    @ResponseStatus(HttpStatus.OK)
    public MemberResponseDTO getMember(@PathVariable String username) {
        return memberService.readByUsername(username);
    }
}