package com.prgrmsfinal.skypedia.reply.util;

import com.prgrmsfinal.skypedia.member.mapper.MemberMapper;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;

public class ReplyMapper {
	public static ReplyResponseDTO.Read toDTO(Reply reply, boolean liked, Long likes) {
		return ReplyResponseDTO.Read.builder()
			.id(reply.getId())
			.parentId((reply.getParentReply() != null) ? reply.getParentReply().getId() : null)
			.author(MemberMapper.toDTO(reply.getMember()))
			.content((reply.isDeleted()) ? "삭제된 댓글입니다." : reply.getContent())
			.deleted(reply.isDeleted())
			.liked(liked)
			.likes(likes)
			.repliedAt(reply.getUpdatedAt())
			.build();
	}
}
