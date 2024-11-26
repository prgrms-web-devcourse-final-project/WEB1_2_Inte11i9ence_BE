package com.prgrmsfinal.skypedia.member.controller;

import com.prgrmsfinal.skypedia.member.dto.MemberRequestDTO;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    /** 내 계정 조회 */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentMember(Authentication authentication) {
        Member member = memberService.getAuthenticatedMember(authentication);
        MemberResponseDTO memberResponseDTO = memberService.read(member.getId());
        return ResponseEntity.ok(new ApiResponse<>("성공적으로 작동했습니다.", memberResponseDTO));
    }

    /** 내 계정 수정 */
    @PutMapping("/me")
    public ResponseEntity<?> putCurrentMember(Authentication authentication, @RequestBody MemberRequestDTO memberRequestDTO) {
        Member member = memberService.getAuthenticatedMember(authentication);
        memberService.modify(member.getId(), memberRequestDTO);
        MemberResponseDTO memberResponseDTO = new MemberResponseDTO(member);
        return ResponseEntity.ok(new ApiResponse<>("성공적으로 작동했습니다.", memberResponseDTO));
    }

    /** 내 계정 탈퇴 */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentMember(Authentication authentication) {
        Member member = memberService.getAuthenticatedMember(authentication);
        memberService.deleteMember(member.getId());
        return ResponseEntity.ok(new ApiResponse<>("성공적으로 작동했습니다.", null));
    }

    /** 타인 계정 조회 */
    @GetMapping("/{username}")
    public ResponseEntity<?> getMember(@PathVariable String username) {
        MemberResponseDTO memberResponseDTO = memberService.readByUsername(username);
        return ResponseEntity.ok(new ApiResponse<>("성공적으로 작동했습니다.", memberResponseDTO));
    }
}