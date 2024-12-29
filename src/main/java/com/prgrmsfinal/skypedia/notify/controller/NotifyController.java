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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notify")
@Validated
@Tag(name = "알림 API 컨트롤러", description = "알림과 관련된 REST API를 제공하는 컨트롤러입니다.")
public class NotifyController {
	private final NotifyService notifyService;

	@Operation(
		summary = "알림 서버 구독",
		description = "알림을 전달받기 위해, 회원 전용 알림 서버를 구독합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "알림 서버를 구독합니다.",
				content = @Content(
					mediaType = "text/event-stream"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "Last-Event-ID",
			description = "마지막 이벤트 ID",
			required = false,
			example = "1",
			schema = @Schema(type = "string", defaultValue = "")
		)
	})
	@GetMapping(value = "/subscribe", produces = "text/event-stream")
	public SseEmitter subscribe(Authentication authentication,
		@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
		return notifyService.subscribe(authentication, lastEventId);
	}

	@Operation(
		summary = "알림 전체 조회",
		description = "회원의 읽음 여부를 조건으로 알림을 전부 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "성공적으로 알림을 조회했습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "401",
				description = "회원이 인증되지 않았습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "알림이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<NotifyResponseDTO.Send> readAll(Authentication authentication,
		@RequestParam(value = "read", required = false, defaultValue = "false") boolean read) {
		return notifyService.readAll(authentication, read);
	}

	@Operation(
		summary = "알림 읽음 처리",
		description = "회원이 읽지않은 알림을 읽음 처리합니다.",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "성공적으로 읽음 처리합니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "401",
				description = "회원이 인증되지 않았습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "읽지않은 알림이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@Parameters({
		@Parameter(
			name = "notifyId",
			description = "알림 ID",
			required = true,
			example = "1",
			schema = @Schema(type = "long", minimum = "1")
		)
	})
	@PostMapping("/{notifyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void check(Authentication authentication,
		@PathVariable("notifyId") @Min(value = 1, message = "ID는 1이상의 값이어야 합니다.") Long notifyId) {
		notifyService.checkRead(authentication, notifyId);
	}

	@Operation(
		summary = "알림 읽음 처리 (전체)",
		description = "회원이 읽지않은 알림을 전부 읽음 처리합니다.",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "성공적으로 읽음 처리합니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "401",
				description = "회원이 인증되지 않았습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@ApiResponse(
				responseCode = "404",
				description = "읽지않은 알림이 존재하지 않습니다.",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void checkAll(Authentication authentication) {
		notifyService.checkRead(authentication);
	}
}
