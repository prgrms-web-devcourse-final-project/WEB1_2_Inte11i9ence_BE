package com.prgrmsfinal.skypedia.member.dto;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MemberResponseDTO {    //회원 조회용 DTO
	private final Long id;
	private final String oauthId;
	private final String name;
	private final String email;
	private final String username;
	private final Role role;
	private final String profileImage;

	public MemberResponseDTO(Member member) {
		this.id = member.getId();
		this.oauthId = member.getOauthId();
		this.name = member.getName();
		this.email = member.getEmail();
		this.username = member.getUsername();
		this.role = member.getRole();
		this.profileImage = member.getProfileImage();
	}

	@Schema(title = "회원 정보 조회 DTO", description = "회원 정보 조회에 사용하는 DTO입니다.")
	@Getter
	@AllArgsConstructor
	public static class Info {
		@Schema(title = "닉네임", description = "회원 닉네임입니다.", example = "닉네임1")
		private final String username;

		@Schema(title = "사진 URL", description = "사진 URL입니다.", minimum = "1", example = "25")
		private final String profileUrl;
	}
}
