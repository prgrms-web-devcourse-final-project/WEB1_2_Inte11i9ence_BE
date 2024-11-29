package com.prgrmsfinal.skypedia.member.controller;

import com.prgrmsfinal.skypedia.member.dto.MemberRequestDTO;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    /** 내 계정 조회 */
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponseDTO getCurrentMember(Authentication authentication) {
        Member member = memberService.getAuthenticatedMember(authentication);
        return memberService.read(member.getId());
    }

    /** 내 계정 수정 */
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
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 성공 시 응답 본문 없음
    public void deleteCurrentMember(Authentication authentication) {
        Member member = memberService.getAuthenticatedMember(authentication);
        memberService.deleteMember(member.getId());
    }

    /** 타인 계정 조회 */
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponseDTO getMember(@PathVariable String username) {
        return memberService.readByUsername(username);
    }
}