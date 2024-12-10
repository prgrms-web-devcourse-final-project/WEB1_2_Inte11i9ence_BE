package com.prgrmsfinal.skypedia.member.mapper;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;

public class MemberMapper {
	public static MemberResponseDTO.Info toDTO(Member member) {
		return new MemberResponseDTO.Info(member.getUsername(), member.getProfileImage());
	}
}
