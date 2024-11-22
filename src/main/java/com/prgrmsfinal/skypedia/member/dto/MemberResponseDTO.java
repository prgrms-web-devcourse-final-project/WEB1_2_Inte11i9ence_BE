package com.prgrmsfinal.skypedia.member.dto;

import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MemberResponseDTO {
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
