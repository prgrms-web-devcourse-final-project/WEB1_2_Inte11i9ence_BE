package com.prgrmsfinal.skypedia.notify.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.prgrmsfinal.skypedia.notify.dto.NotifyResponseDTO;
import com.prgrmsfinal.skypedia.notify.entity.Notify;

public interface NotifyService {
	SseEmitter subscribe(Authentication authentication, String lastEventId);

	void send(Object data);

	void send(String username, Object data);

	void saveAll(List<Notify> notifies);

	void save(Notify notify);

	List<NotifyResponseDTO.Send> readAll(Authentication authentication, boolean read);

	void checkRead(Authentication authentication, Long notifyId);

	void checkRead(Authentication authentication);
}
