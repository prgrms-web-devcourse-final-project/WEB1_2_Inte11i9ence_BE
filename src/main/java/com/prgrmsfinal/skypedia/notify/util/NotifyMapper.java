package com.prgrmsfinal.skypedia.notify.util;

import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.notify.dto.NotifyRequestDTO;
import com.prgrmsfinal.skypedia.notify.dto.NotifyResponseDTO;
import com.prgrmsfinal.skypedia.notify.entity.Notify;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotifyMapper {
	private final MemberService memberService;

	public static NotifyResponseDTO.Send toDTO(Notify notify) {
		return NotifyResponseDTO.Send.builder()
			.content(notify.getContent())
			.type(notify.getNotifyType().getName())
			.sentAt(notify.getSentAt())
			.uri(notify.getUri())
			.read(notify.isRead())
			.build();
	}

	public static Notify toEntity(NotifyRequestDTO.User dto) {
		return Notify.builder()
			.member(dto.getMember())
			.notifyType(dto.getNotifyType())
			.content(dto.getContent())
			.sentAt(dto.getSentAt())
			.uri(dto.getUri())
			.read(false)
			.build();
	}

	public static Notify toEntity(NotifyRequestDTO.Global dto) {
		return Notify.builder()
			.member(null)
			.notifyType(dto.getNotifyType())
			.content(dto.getContent())
			.sentAt(dto.getSentAt())
			.uri(dto.getUri())
			.read(false)
			.build();
	}

	public static NotifyResponseDTO.Send convert(NotifyRequestDTO.User dto) {
		return NotifyResponseDTO.Send.builder()
			.content(dto.getContent())
			.type(dto.getNotifyType().getName())
			.sentAt(dto.getSentAt())
			.uri(dto.getUri())
			.read(false)
			.build();
	}

	public static NotifyResponseDTO.Send convert(NotifyRequestDTO.Global dto) {
		return NotifyResponseDTO.Send.builder()
			.content(dto.getContent())
			.type(dto.getNotifyType().getName())
			.sentAt(dto.getSentAt())
			.uri(dto.getUri())
			.read(false)
			.build();
	}
}
