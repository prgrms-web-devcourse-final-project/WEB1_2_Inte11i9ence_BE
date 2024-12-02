package com.prgrmsfinal.skypedia.reply.service;

import java.util.List;

import com.prgrmsfinal.skypedia.reply.dto.ReplyDTO;

public interface ReplyService {
	List<ReplyDTO> readAll(ReplyDTO replyDTO);

	ReplyDTO register(ReplyDTO replyDTO);

	ReplyDTO update(ReplyDTO replyDTO);

	void delete(Long id);
}
