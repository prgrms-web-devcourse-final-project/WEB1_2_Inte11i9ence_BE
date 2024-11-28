package com.prgrmsfinal.skypedia.oauth2.service;

import com.prgrmsfinal.skypedia.member.dto.MemberDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.oauth2.dto.CustomOAuth2User;
import com.prgrmsfinal.skypedia.oauth2.dto.GoogleResponse;
import com.prgrmsfinal.skypedia.oauth2.dto.NaverResponse;
import com.prgrmsfinal.skypedia.oauth2.dto.OAuth2Response;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService { //회원가입, 로그인 기능 클래스 (검증포함)

    private final MemberRepository memberRepository;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if(registrationId.equals("naver")){

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

        }
        else if(registrationId.equals("google")){

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        }
        else{
            return null;
        }
        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String oauthId = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        Member existData = memberRepository.findByOauthId(oauthId);

        //신규 사용자일 경우
        if (existData == null) {
            // 새로운 회원 생성
            String randomUsername = generateRandomUsername();
            Member member = Member.builder()
                    .oauthId(oauthId)
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .username(randomUsername)
                    .profileImage(null) // 기본값으로 null
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .role("ROLE_USER")
                    .build();

            // 회원 저장
            memberRepository.save(member);

            // DTO 생성
            MemberDTO memberDTO = MemberDTO.builder()
                    .oauthId(oauthId)
                    .name(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .build();

            // CustomOAuth2User 반환
            return new CustomOAuth2User(memberDTO);
        } else {
            // 기존 회원 정보 갱신
            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());
            memberRepository.save(existData);

            // DTO 생성
            MemberDTO memberDTO = MemberDTO.builder()
                    .oauthId(existData.getOauthId())
                    .name(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .build();

            // CustomOAuth2User 반환
            return new CustomOAuth2User(memberDTO);
        }
    }
    private String generateRandomUsername(){
        return "user"+ RandomStringUtils.randomAlphanumeric(8);
    }
}
