package com.prgrmsfinal.skypedia.planShare.service;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupResponseDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

public interface PlanGroupService {
	PlanGroupResponseDTO.ReadAll readAll(Authentication authentication, String standard, String regionName, int page);

	PlanGroupResponseDTO.Read read(Authentication authentication, Long planGroupId);

	PlanGroupResponseDTO.ReadAll readByMember(String username, int page);

	PlanGroupResponseDTO.ReadAll readByScrap(Authentication authentication, int page);

	ReplyResponseDTO.ReadAll readReplies(Authentication authentication, Long planGroupId, int page);

	void create(Authentication authentication, PlanGroupRequestDTO.Create groupCreate);

	void createReply(Authentication authentication, Long planGroupId, PlanGroupRequestDTO.CreateReply groupCreateReply);

	PlanGroupResponseDTO.LikeStatus toggleLikes(Authentication authentication, Long planGroupId);

	boolean toggleScrap(Authentication authentication, Long planGroupId);

	String update(Authentication authentication, Long planGroupId, PlanGroupRequestDTO.Update groupUpdate);

	void delete(Authentication authentication, Long planGroupId);

	void restore(Authentication authentication, Long planGroupId);

	PlanGroupResponseDTO.ReadAll search(String keyword, String order, int page);
}
