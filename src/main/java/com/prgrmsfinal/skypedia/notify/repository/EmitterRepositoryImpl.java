package com.prgrmsfinal.skypedia.notify.repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository {
	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${emitter.prefix.key}")
	private String EMITTER_KEY_PREFIX;

	@Value("${event.prefix.key}")
	private String EVENT_KEY_PREFIX;

	@Override
	public Map<String, SseEmitter> findAllEmitters(String username) {
		String keyword = EMITTER_KEY_PREFIX + username + "*";

		return redisTemplate.keys(keyword).stream().collect(Collectors.toMap(
			key -> key,
			key -> (SseEmitter)redisTemplate.opsForValue().get(key)
		));
	}

	@Override
	public Map<String, Object> findAllEvents(String username) {
		String keyword = EVENT_KEY_PREFIX + username + "*";

		return redisTemplate.keys(keyword).stream().collect(Collectors.toMap(
			key -> key,
			key -> redisTemplate.opsForValue().get(key)
		));
	}

	@Override
	public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
		String key = EMITTER_KEY_PREFIX + emitterId;

		redisTemplate.opsForValue().set(key, sseEmitter, 1, TimeUnit.HOURS);

		return sseEmitter;
	}

	@Override
	public void saveEvent(String eventId, Object event) {
		String key = EVENT_KEY_PREFIX + eventId;

		redisTemplate.opsForValue().set(key, event, 1, TimeUnit.HOURS);
	}

	@Override
	public void delete(String emitterId) {
		String key = EMITTER_KEY_PREFIX + emitterId;

		redisTemplate.delete(key);
	}

	@Override
	public void deleteAllEmitters(String username) {
		String keyword = EMITTER_KEY_PREFIX + username + "*";

		redisTemplate.keys(keyword).forEach(redisTemplate::delete);
	}

	@Override
	public void deleteAllEvents(String username) {
		String keyword = EVENT_KEY_PREFIX + username + "*";

		redisTemplate.keys(keyword).forEach(redisTemplate::delete);
	}
}
