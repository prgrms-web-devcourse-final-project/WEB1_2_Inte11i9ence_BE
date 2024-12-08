package com.prgrmsfinal.skypedia.reply.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReplyDTO {

	private Long id;

	private Long memberId;

	private String content;

	private Long parentReplyId;

	private List<Long> childReplyIds;

	private Long likes;

	private Boolean deleted;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;
}
