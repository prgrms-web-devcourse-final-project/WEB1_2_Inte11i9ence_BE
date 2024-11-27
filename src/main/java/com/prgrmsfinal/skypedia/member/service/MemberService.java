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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;



@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberService {
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;    // 필터를 사용할 엔티티 매니저 주입

    //회원 수정
    public void modify(Long id, MemberRequestDTO memberRequestDTO) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberError.NOT_FOUND::get);

        if (memberRequestDTO.getUsername() != null) {
            member.setUsername(memberRequestDTO.getUsername());
        }
        if (memberRequestDTO.getProfileImage() != null) {
            member.setProfileImage(memberRequestDTO.getProfileImage());
        }
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    //회원 조회
    public MemberResponseDTO read(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberError.NOT_FOUND::get);
        return new MemberResponseDTO(member);  // ResponseEntity로 감싸지 않고 MemberResponseDTO만 반환
    }


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
        // Hibernate Session을 가져옵니다.
        Session session = entityManager.unwrap(Session.class);

        // 필터 비활성화: 논리 삭제된 데이터도 조회 가능
        session.disableFilter("withdrawnFilter");

        // 탈퇴한 회원 조회 (withdrawn = true, withdrawnAt < 두 달 전)
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);
        List<Member> membersToDelete = memberRepository.findByWithdrawnTrueAndWithdrawnAtBefore(twoMonthsAgo);

        // 물리 삭제
        memberRepository.deleteAll(membersToDelete);

        // 필터 다시 활성화
        session.enableFilter("withdrawnFilter");

    }
}
