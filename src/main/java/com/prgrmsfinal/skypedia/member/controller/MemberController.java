package com.prgrmsfinal.skypedia.member.controller;

import com.prgrmsfinal.skypedia.member.dto.MemberRequestDTO;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

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


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable Long id,
                                               Authentication authentication) throws AccessDeniedException {
        authenticateMember(id, authentication);

        memberService.deleteMember(id);

        return ResponseEntity.ok("Member deleted");
    }

    // 회원 인증 로직을 확인하는 공통 메서드
    private void authenticateMember(Long id, Authentication authentication) throws AccessDeniedException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String authenticatedOauthId = customOAuth2User.getOauthId();

        // 회원 정보 조회
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalStateException("Member not found"));

        // oauthId 일치 여부 검사
        if (!authenticatedOauthId.equals(member.getOauthId())) {
            throw new AccessDeniedException("Oauth id mismatch"); // 권한 거부 예외
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // JWT 쿠키 삭제
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);  // 쿠키 만료
        cookie.setPath("/");
        response.addCookie(cookie);

        // 세션 무효화
        HttpSession session = request.getSession(false); // 현재 세션 가져오기, 없으면 null 반환
        if (session != null) {
            session.invalidate();  // 세션 무효화
        }

        // Spring Security에서 인증 정보 제거
        SecurityContextHolder.clearContext();  // 인증 정보 제거

        // 로그아웃 후 로그인 페이지로 리디렉션
        return "redirect:/";  // 또는 적절한 페이지로 리디렉션
    }
}
