package com.prgrmsfinal.skypedia.oauth2.service;

import com.prgrmsfinal.skypedia.member.dto.MemberDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.Role;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.oauth2.dto.CustomOAuth2User;
import com.prgrmsfinal.skypedia.oauth2.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberCreationServiceImpl implements MemberCreationService {

    private final MemberRepository memberRepository;

    @Override
    public CustomOAuth2User createNewMember(String oauthId, OAuth2Response oauth2Response) {
        String randomUsername = generateRandomUsername();

        Member member = Member.builder()
                .oauthId(oauthId)
                .email(oauth2Response.getEmail())
                .name(oauth2Response.getName())
                .username(randomUsername)
                .profileImage(null)
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(member);

        MemberDTO memberDTO = MemberDTO.builder()
                .oauthId(oauthId)
                .name(oauth2Response.getName())
                .role(Role.ROLE_USER)
                .build();

        return new CustomOAuth2User(memberDTO);
    }

    @Override
    public CustomOAuth2User updateExistingMember(Member existData, OAuth2Response oauth2Response) {
        existData.setEmail(oauth2Response.getEmail());
        existData.setName(oauth2Response.getName());
        memberRepository.save(existData);

        MemberDTO memberDTO = MemberDTO.builder()
                .oauthId(existData.getOauthId())
                .name(oauth2Response.getName())
                .role(existData.getRole())
                .build();

        return new CustomOAuth2User(memberDTO);
    }

    private String generateRandomUsername(){
        return "user"+ RandomStringUtils.randomAlphanumeric(8);
    }
}
