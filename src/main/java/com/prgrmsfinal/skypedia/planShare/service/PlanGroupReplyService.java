package com.prgrmsfinal.skypedia.planShare.service;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.planShare.entity.PlanGroup;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;

public interface PlanGroupReplyService {
	ReplyResponseDTO.ReadAll readAll(Authentication authentication, Long planGroupId, Long lastReplyId);

	void create(PlanGroup planGroup, Reply reply);

	Long getReplyCount(Long planGroupId);
}
