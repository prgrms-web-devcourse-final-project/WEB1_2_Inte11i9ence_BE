package com.prgrmsfinal.skypedia.reply.service;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;

public interface ReplyService {
	ReplyResponseDTO.ReadAll readAll(Authentication authentication, Long parentId, int page);

	Reply create(ReplyRequestDTO.Create request);

	void modify(Authentication authentication, Long replyId, ReplyRequestDTO.Modify request);

	void delete(Authentication authentication, Long replyId);

	void restore(Authentication authentication, Long replyId);

	ReplyResponseDTO.LikeStatus toggleLikes(Authentication authentication, Long replyId);

	boolean getLiked(Long memberId, Long replyId);

	Long getLikes(Long replyId);

	boolean isCurrentMemberLiked(Authentication authentication, Reply result);

	Reply create(PlanGroupRequestDTO.CreateReply groupCreateReply, Member member);
}
