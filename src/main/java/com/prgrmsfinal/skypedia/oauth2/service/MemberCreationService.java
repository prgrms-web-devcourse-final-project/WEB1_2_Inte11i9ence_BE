package com.prgrmsfinal.skypedia.oauth2.service;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.oauth2.dto.CustomOAuth2User;
import com.prgrmsfinal.skypedia.oauth2.dto.OAuth2Response;

public interface MemberCreationService {
    CustomOAuth2User createNewMember(String oauthId, OAuth2Response oauth2Response);
    CustomOAuth2User updateExistingMember(Member existData, OAuth2Response oauth2Response);
}
