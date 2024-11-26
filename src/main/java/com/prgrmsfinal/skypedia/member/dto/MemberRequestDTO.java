package com.prgrmsfinal.skypedia.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberRequestDTO { //회원 수정용 DTO

    @NotBlank(message = "닉네임에는 공백이 들어갈 수 없습니다.")
    private String username;

    private String profileImage;
}
