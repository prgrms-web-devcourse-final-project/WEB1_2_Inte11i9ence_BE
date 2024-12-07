package com.prgrmsfinal.skypedia.reply.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.reply.dto.ReplyRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reply")
@Log4j2
public class ReplyController {
	private final ReplyService replyService;

	@GetMapping("/{parentId}")
	@ResponseStatus(HttpStatus.OK)
	public ReplyResponseDTO.ReadAll readAll(Authentication authentication, @PathVariable("parentId") Long parentId,
		@RequestParam("lastidx") Long lastReplyId) {
		return replyService.readAll(authentication, parentId, lastReplyId);
	}

	@PostMapping("/{replyId}/likes")
	@ResponseStatus(HttpStatus.OK)
	public ReplyResponseDTO.ToggleLikes toggleLikes(Authentication authentication,
		@PathVariable("replyId") Long replyId) {
		return replyService.toggleLikes(authentication, replyId);
	}

	@PutMapping("/{replyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void modify(Authentication authentication, @PathVariable("replyId") Long replyId,
		ReplyRequestDTO.Modify request) {
		replyService.modify(authentication, replyId, request);
	}

	@DeleteMapping("/{replyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(Authentication authentication, @PathVariable("replyId") Long replyId) {
		replyService.delete(authentication, replyId);
	}

	@PatchMapping("/{replyId}/restore")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void restore(Authentication authentication, @PathVariable("replyId") Long replyId) {
		replyService.restore(authentication, replyId);
	}
}
