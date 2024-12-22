package com.prgrmsfinal.skypedia.notify.repository;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
	Map<String, SseEmitter> findAllEmitters(String username);

	Map<String, Object> findAllEvents(String username);

	SseEmitter save(String emitterId, SseEmitter sseEmitter);

	void saveEvent(String eventId, Object object);

	void delete(String emitterId);

	void deleteAllEmitters(String username);

	void deleteAllEvents(String username);
}
