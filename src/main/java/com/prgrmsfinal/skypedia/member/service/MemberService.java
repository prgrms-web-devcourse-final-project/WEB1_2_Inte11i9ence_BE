package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.member.dto.MemberDTO;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDTO;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberService {
    private final MemberRepository memberRepository;

    public void modify(Long id, MemberRequestDTO memberRequestDTO) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        if (memberRequestDTO.getUsername() != null) {
            member.setUsername(memberRequestDTO.getUsername());
        }
        if (memberRequestDTO.getProfileImage() != null) {
            member.setProfileImage(memberRequestDTO.getProfileImage());
        }
        memberRepository.save(member);
    }

    public MemberResponseDTO read(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + id));
        return new MemberResponseDTO(member);  // ResponseEntity로 감싸지 않고 MemberResponseDTO만 반환
    }
}
