package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.planShare.entity.PlanGroup;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroupReply;
import com.prgrmsfinal.skypedia.planShare.exception.PlanError;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupReplyRepository;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import com.prgrmsfinal.skypedia.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanGroupReplyServiceImpl implements PlanGroupReplyService {
	private final PlanGroupReplyRepository planGroupReplyRepository;

	private final ReplyService replyService;

	@Override
	public ReplyResponseDTO.ReadAll readAll(Authentication authentication, Long planGroupId, Long lastReplyId) {
		List<Reply> results = planGroupReplyRepository.findRepliesByPlanGroupId(planGroupId, lastReplyId,
			PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "repliedAt")));

		if (results == null || results.isEmpty()) {
			throw PlanError.NOT_FOUND_REPLIES.getException();
		}

		Reply lastReply = results.get(results.size() - 1);

		String nextUri = new StringBuilder()
			.append("/api/v1/post/").append(planGroupId)
			.append("/reply?lastidx=").append(lastReply.getId())
			.toString();

		return new ReplyResponseDTO.ReadAll(null, nextUri);
	}

	@Override
	public void create(PlanGroup planGroup, Reply reply) {
		planGroupReplyRepository.save(PlanGroupReply.builder()
			.planGroup(planGroup)
			.reply(reply)
			.build());
	}

	@Override
	public Long getReplyCount(Long planGroupId) {
		return planGroupReplyRepository.countRepliesByPlanGroupId(planGroupId);
	}
}
