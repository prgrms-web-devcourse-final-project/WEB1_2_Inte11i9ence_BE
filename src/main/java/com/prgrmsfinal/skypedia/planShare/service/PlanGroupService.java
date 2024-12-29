package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupResponseDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

public interface PlanGroupService {
	List<PlanGroupResponseDTO.Info> readAll(Authentication authentication,
		PlanGroupResponseDTO.ReadAll groupReadAll);

	PlanGroupResponseDTO.Read read(Authentication authentication, Long id);

	List<String> create(Authentication authentication, PlanGroupRequestDTO.Create groupCreate);

	List<String> update(Authentication authentication, Long id, PlanGroupRequestDTO.Update groupUpdate);

	void delete(Authentication authentication, Long id);

	PlanGroupResponseDTO.ReadAll readByRegion(String regionName, Long lastPlanGroupId);

	PlanGroupResponseDTO.ReadAll readByMember(String username, Long lastPlanGroupId);

	ReplyResponseDTO.ReadAll readReplies(Authentication authentication, Long planGroupId, Long lastReplyId);

	PlanGroupResponseDTO.ReadAll search(String keyword, String target, String cursor, Long lastPlanGroupId);

	void createReply(Authentication authentication, Long planGroupId, PlanGroupRequestDTO.CreateReply groupCreateReply);

	PlanGroupResponseDTO.ToggleLikes toggleLikes(Authentication authentication, Long planGroupId);

	boolean toggleScrap(Authentication authentication, Long planGroupId);
}
