package com.prgrmsfinal.skypedia.member.service;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.member.dto.MemberRequestDTO;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;

public interface MemberService {
	Member getAuthenticatedMember(Authentication authentication);

	void modify(Long id, MemberRequestDTO memberRequestDTO);

	MemberResponseDTO read(Long id);

	MemberResponseDTO readByUsername(String username);

	void deleteMember(Long id);

	void physicalDeleteMember();

	boolean checkExistsByUsername(String username);

	Member getByUsername(String username);
}
