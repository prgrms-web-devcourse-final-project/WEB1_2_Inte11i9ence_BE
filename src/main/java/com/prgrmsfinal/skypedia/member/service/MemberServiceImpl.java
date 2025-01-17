package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.member.dto.MemberRequestDTO;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.exception.MemberError;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.hibernate.Session;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;    // 필터를 사용할 엔티티 매니저 주입

    public Member getAuthenticatedMember(Authentication authentication) {
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication is required");
        }

        String email;
        if (authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            Map<String, Object> attributes = ((OAuth2User) authentication.getPrincipal()).getAttributes();
            // Naver의 경우 response 내부에 실제 사용자 정보가 있음
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
        } else {
            throw new AuthenticationCredentialsNotFoundException("Unsupported authentication type");
        }

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("Member not found with email: " + email);
        }
        return member;
    }

    //회원 수정
    @Transactional
    public void modify(Long id, MemberRequestDTO memberRequestDTO) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberError.NOT_FOUND::get);

        if (memberRequestDTO.getUsername() != null) {
            member.setUsername(memberRequestDTO.getUsername());
        }
        if (memberRequestDTO.getProfileImage() != null) {
            member.setProfileImage(memberRequestDTO.getProfileImage());
        }
        memberRepository.save(member);
    }

    //회원 조회
    @Transactional(readOnly = true)
    public MemberResponseDTO read(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberError.NOT_FOUND::get);
        return new MemberResponseDTO(member);  // ResponseEntity로 감싸지 않고 MemberResponseDTO만 반환
    }

    public MemberResponseDTO readByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(MemberError.NOT_FOUND::get);
        return new MemberResponseDTO(member);
    }


    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberError.NOT_FOUND::get);

        // 논리 삭제 처리 (withdrawn을 true로 설정)
        member.setWithdrawn(true);
        member.setWithdrawnAt(LocalDateTime.now());

        // oauthId에 "-deactivated" 추가
        String oauthId = member.getOauthId();
        if (oauthId != null && !oauthId.endsWith("-deactivated")) {
            member.setOauthId(oauthId + "-deactivated");
        }

        // 수정된 회원 엔티티 저장
        memberRepository.save(member);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?") // 매달 1일 자정에 실행
    public void physicalDeleteMember() {
        Session session = entityManager.unwrap(Session.class);

        try {
            // 필터 비활성화: 논리 삭제된 데이터도 조회 가능
            session.disableFilter("withdrawnFilter");

            // 탈퇴한 회원 조회 (withdrawn = true, withdrawnAt < 두 달 전)
            LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);
            List<Member> membersToDelete = memberRepository.findByWithdrawnTrueAndWithdrawnAtBefore(twoMonthsAgo);

            // 물리 삭제
            memberRepository.deleteAll(membersToDelete);

        } catch (Exception e) {
            // 로깅 추가
            log.error("회원 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("회원 삭제 처리 중 오류가 발생했습니다.", e);

        } finally {
            // 반드시 필터 다시 활성화
            session.enableFilter("withdrawnFilter");
        }
    }

    @Override
    public boolean checkExistsByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    @Override
    public Member getByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }
}