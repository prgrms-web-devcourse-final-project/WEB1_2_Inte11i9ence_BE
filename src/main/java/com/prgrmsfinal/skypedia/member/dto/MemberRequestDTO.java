package com.prgrmsfinal.skypedia.member.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberRequestDTO {

    private String username;

    private String profileImage;
}
