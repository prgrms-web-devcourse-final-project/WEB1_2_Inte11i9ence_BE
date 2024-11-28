package com.prgrmsfinal.skypedia.reply.service;

import com.prgrmsfinal.skypedia.reply.dto.ReplyDTO;

import java.util.List;

public interface ReplyService {
    List<ReplyDTO> readAll(ReplyDTO replyDTO);

    ReplyDTO register(ReplyDTO replyDTO);

    ReplyDTO update(ReplyDTO replyDTO);

    void delete(Long id);
}
