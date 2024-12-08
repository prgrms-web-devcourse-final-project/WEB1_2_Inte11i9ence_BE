package com.prgrmsfinal.skypedia.oauth2.jwt;

import com.prgrmsfinal.skypedia.member.entity.Member;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustomUserDetails extends User {
    private final Long id;
    private final String name;
    private final String profileImage;

    public CustomUserDetails(Long id, String email, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String name, String profileImage) {
        super(email, password != null ? password : "", true, true, true, true,
                authorities != null ? authorities : Collections.emptyList());
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
    }

    // 대신 정적 팩토리 메서드 사용
    public static CustomUserDetails from(Member member) {
        return new CustomUserDetails(
                member.getId(),
                member.getEmail(),
                "",
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
                member.getName(),
                member.getProfileImage()
        );
    }
}