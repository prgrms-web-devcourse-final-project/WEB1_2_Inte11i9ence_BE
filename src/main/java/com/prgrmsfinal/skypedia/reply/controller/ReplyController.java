package com.prgrmsfinal.skypedia.reply.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.reply.dto.ReplyDTO;
import com.prgrmsfinal.skypedia.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reply")
@Log4j2
public class ReplyController {
	private final ReplyService replyService;

	@GetMapping
	public ResponseEntity<List<ReplyDTO>> readAll(ReplyDTO replyDTO) {
		return ResponseEntity.ok(replyService.readAll(replyDTO));
	}

	@PostMapping
	public ResponseEntity<ReplyDTO> create(@RequestBody ReplyDTO replyDTO) {
		return ResponseEntity.ok(replyService.register(replyDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ReplyDTO> update(@PathVariable("id") Long id, @RequestBody ReplyDTO replyDTO) {
		replyDTO.setId(id);
		return ResponseEntity.ok(replyService.update(replyDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable("id") Long id) {
		replyService.delete(id);
		return ResponseEntity.ok(Map.of("message", "삭제 완료"));
	}
}
