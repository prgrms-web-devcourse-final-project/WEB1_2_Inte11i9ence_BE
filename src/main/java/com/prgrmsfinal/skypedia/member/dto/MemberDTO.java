package com.prgrmsfinal.skypedia.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDTO {    //페이로드용 DTO

	private String role;
	private String name;
	private String oauthId;
}
