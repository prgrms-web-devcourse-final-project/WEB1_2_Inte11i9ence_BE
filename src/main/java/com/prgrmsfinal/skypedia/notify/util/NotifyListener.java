package com.prgrmsfinal.skypedia.notify.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.prgrmsfinal.skypedia.notify.dto.NotifyRequestDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotifyListener {
	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${notify.user.prefix.key}")
	private String NOTIFY_USER_PREFIX;

	@Value("${notify.global.prefix.key}")
	private String NOTIFY_GLOBAL_PREFIX;

	@EventListener
	public void handleUserEvent(NotifyRequestDTO.User dto) {
		String key = NOTIFY_USER_PREFIX + dto.getMember().getUsername();

		redisTemplate.opsForList().rightPush(key, dto);
	}

	@EventListener
	public void handleGlobalEvent(NotifyRequestDTO.Global dto) {
		String key = NOTIFY_GLOBAL_PREFIX + System.currentTimeMillis();

		redisTemplate.opsForValue().set(key, dto, 100, TimeUnit.SECONDS);
	}
}
