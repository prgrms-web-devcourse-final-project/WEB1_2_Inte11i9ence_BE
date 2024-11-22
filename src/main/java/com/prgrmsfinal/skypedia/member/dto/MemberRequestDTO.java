package com.prgrmsfinal.skypedia.member.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberRequestDTO { //회원 수정용 DTO

    private String username;

    private String profileImage;
}
