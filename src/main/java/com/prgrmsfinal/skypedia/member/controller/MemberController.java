package com.prgrmsfinal.skypedia.member.controller;

import com.prgrmsfinal.skypedia.member.dto.MemberRequestDTO;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PutMapping("/{id}")
    public ResponseEntity<String> updateMember(@PathVariable Long id,
                                               @RequestBody MemberRequestDTO memberRequestDTO,
                                               Authentication authentication) {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String authenticatedOauthId = customOAuth2User.getOauthId();

        Member member = memberRepository.findById(id).orElseThrow(()-> new IllegalStateException("Member not found"));

        if (!authenticatedOauthId.equals(member.getOauthId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Oauth id mismatch");
        }

        memberService.modify(id, memberRequestDTO);

        return ResponseEntity.ok("Member updated");
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> getMember(@PathVariable Long id) {
        MemberResponseDTO memberResponseDTO = memberService.read(id);  // MemberResponseDTO를 받음
        return ResponseEntity.ok(memberResponseDTO);  // ResponseEntity로 감싸서 반환
    }
}
