package com.prgrmsfinal.skypedia.member.dto;

import com.prgrmsfinal.skypedia.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDTO {    //페이로드용 DTO

    private Role role;
    private String name;
    private String oauthId;

}
