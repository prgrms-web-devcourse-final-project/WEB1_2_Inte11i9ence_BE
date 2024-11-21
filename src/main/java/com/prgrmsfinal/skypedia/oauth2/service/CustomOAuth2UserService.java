package com.prgrmsfinal.skypedia.oauth2.service;

import com.prgrmsfinal.skypedia.member.dto.MemberDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.oauth2.dto.CustomOAuth2User;
import com.prgrmsfinal.skypedia.oauth2.dto.GoogleResponse;
import com.prgrmsfinal.skypedia.oauth2.dto.NaverResponse;
import com.prgrmsfinal.skypedia.oauth2.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

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
        if(existData == null){
            Member member = new Member();
            member.setOauthId(oauthId);
            member.setEmail(oAuth2Response.getEmail());
            member.setName(oAuth2Response.getName());

            String randomUsername = generateRandomUsername();
            member.setUsername(randomUsername);
            //member.setProfileImage("default-profile-image-url");

            //권한 설정 및 회원 저장
            member.setRole("ROLE_USER");
            memberRepository.save(member);

            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setOauthId(oauthId);
            memberDTO.setName(oAuth2Response.getName());
            memberDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(memberDTO);
        }
        else {

            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());
            memberRepository.save(existData);

            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setOauthId(existData.getOauthId());
            memberDTO.setName(oAuth2Response.getName());
            memberDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(memberDTO);

        }
    }
    private String generateRandomUsername(){
        return "user"+ RandomStringUtils.randomAlphanumeric(8);
    }
}
