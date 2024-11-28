package com.prgrmsfinal.skypedia.member.dto;

import com.prgrmsfinal.skypedia.member.entity.Member;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;



import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberResponseDTO {    //회원 조회용 DTO
    private Long id;
    private String oauthId;
    private String name;
    private String email;
    private String username;
    private String role;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MemberResponseDTO(Member member) {
        this.id = member.getId();
        this.oauthId = member.getOauthId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.role = member.getRole();
        this.profileImage = member.getProfileImage();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
    }
}
