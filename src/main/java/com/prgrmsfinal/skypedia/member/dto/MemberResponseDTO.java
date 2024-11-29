package com.prgrmsfinal.skypedia.member.dto;

import com.prgrmsfinal.skypedia.global.entity.BaseTime;
import com.prgrmsfinal.skypedia.member.entity.Member;


import lombok.Getter;

@Getter
public class MemberResponseDTO extends BaseTime {    //회원 조회용 DTO
    private final Long id;
    private final String oauthId;
    private final String name;
    private final String email;
    private final String username;
    private final String role;
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
}
