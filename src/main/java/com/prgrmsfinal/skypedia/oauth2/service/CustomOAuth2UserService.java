package com.prgrmsfinal.skypedia.oauth2.service;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.oauth2.dto.GoogleResponse;
import com.prgrmsfinal.skypedia.oauth2.dto.NaverResponse;
import com.prgrmsfinal.skypedia.oauth2.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Log4j2
public class CustomOAuth2UserService extends DefaultOAuth2UserService { //검증서비스

    private final MemberRepository memberRepository;
    private final MemberCreationService memberCreationService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.debug(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = switch (registrationId) {
            case "naver" -> new NaverResponse(oAuth2User.getAttributes());
            case "google" -> new GoogleResponse(oAuth2User.getAttributes());
            default -> throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        };

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String oauthId = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        Member existData = memberRepository.findByOauthId(oauthId);

        //신규 사용자일 경우
        if (existData == null) {
            return memberCreationService.createNewMember(oauthId, oAuth2Response);
        } else {
            return memberCreationService.updateExistingMember(existData, oAuth2Response);
        }
    }
}
