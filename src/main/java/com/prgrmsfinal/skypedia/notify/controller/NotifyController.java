package com.prgrmsfinal.skypedia.notify.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.prgrmsfinal.skypedia.notify.dto.NotifyResponseDTO;
import com.prgrmsfinal.skypedia.notify.service.NotifyService;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notify")
@Validated
public class NotifyController {
	private final NotifyService notifyService;

	@GetMapping(value = "/subscribe", produces = "text/event-stream")
	public SseEmitter subscribe(Authentication authentication,
		@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
		return notifyService.subscribe(authentication, lastEventId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<NotifyResponseDTO.Send> readAll(Authentication authentication,
		@RequestParam(value = "read", required = false, defaultValue = "false") boolean read) {
		return notifyService.readAll(authentication, read);
	}

	@PostMapping("/{notifyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void check(Authentication authentication,
		@PathVariable("notifyId") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long notifyId) {
		notifyService.checkRead(authentication, notifyId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void checkAll(Authentication authentication) {
		notifyService.checkRead(authentication);
	}
}
