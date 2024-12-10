package com.prgrmsfinal.skypedia.notify.util;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.notify.dto.NotifyRequestDTO;
import com.prgrmsfinal.skypedia.notify.entity.Notify;
import com.prgrmsfinal.skypedia.notify.service.NotifyService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotifyScheduler {
	private final RedisTemplate<String, Object> redisTemplate;

	private final NotifyService notifyService;

	private final MemberService memberService;

	@Value("${notify.user.prefix.key}")
	private String NOTIFY_USER_PREFIX;

	@Value("${notify.global.prefix.key}")
	private String NOTIFY_GLOBAL_PREFIX;

	@Scheduled(fixedRate = 60000)
	private void sendToUser() {
		Set<String> keys = redisTemplate.keys(NOTIFY_USER_PREFIX + "*");

		if (keys == null || keys.isEmpty()) {
			return;
		}

		keys.forEach(key -> {
			List<Object> dtos = redisTemplate.opsForList().range(key, 0, -1);

			String username = key.replace(NOTIFY_USER_PREFIX, "");

			notifyService.send(username, dtos.stream()
				.map(dto -> NotifyMapper.convert((NotifyRequestDTO.User)dto))
				.toList()
			);

			saveToDB(username, dtos);

			redisTemplate.delete(key);
		});
	}

	@Scheduled(fixedRate = 60000)
	private void sendToGlobal() {
		Set<String> keys = redisTemplate.keys(NOTIFY_GLOBAL_PREFIX + "*");

		if (keys == null || keys.isEmpty()) {
			return;
		}

		keys.forEach(key -> {
			NotifyRequestDTO.Global dto = (NotifyRequestDTO.Global)redisTemplate.opsForValue().get(key);

			notifyService.send(NotifyMapper.convert(dto));

			saveToDB(dto);

			redisTemplate.delete(key);
		});
	}

	private void saveToDB(String username, List<Object> dtos) {
		List<Notify> notifies = dtos.stream()
			.map(dto -> NotifyMapper.toEntity((NotifyRequestDTO.User)dto))
			.toList();

		notifyService.saves(notifies);
	}

	private void saveToDB(Object dto) {
		Notify notify = NotifyMapper.toEntity((NotifyRequestDTO.Global)dto);

		notifyService.save(notify);
	}
}
