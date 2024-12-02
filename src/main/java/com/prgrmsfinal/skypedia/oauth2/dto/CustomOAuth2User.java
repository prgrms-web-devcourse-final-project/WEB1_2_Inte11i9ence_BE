package com.prgrmsfinal.skypedia.oauth2.dto;

import com.prgrmsfinal.skypedia.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final MemberDTO memberDTO;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return memberDTO.getRole().name();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return memberDTO.getName();
    }

    public String getOauthId(){
        return memberDTO.getOauthId();
    }
}
