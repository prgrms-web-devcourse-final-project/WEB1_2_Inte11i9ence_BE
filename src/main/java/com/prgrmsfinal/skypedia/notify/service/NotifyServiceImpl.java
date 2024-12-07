package com.prgrmsfinal.skypedia.notify.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.notify.dto.NotifyResponseDTO;
import com.prgrmsfinal.skypedia.notify.entity.Notify;
import com.prgrmsfinal.skypedia.notify.exception.NotifyError;
import com.prgrmsfinal.skypedia.notify.repository.EmitterRepository;
import com.prgrmsfinal.skypedia.notify.repository.NotifyRepository;
import com.prgrmsfinal.skypedia.notify.util.NotifyMapper;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {
	private final RedisTemplate<String, Object> redisTemplate;

	private final NotifyRepository notifyRepository;

	private final EmitterRepository emitterRepository;

	private final MemberService memberService;

	private static final Long TIME_OUT_MILLISECOND = 3600000L;

	@Value("${emitter.prefix.key}")
	private String EMITTER_KEY_PREFIX;

	@Override
	public SseEmitter subscribe(Authentication authentication, String lastEventId) {
		String username = memberService.getAuthenticatedMember(authentication).getUsername();
		String emitterId = generateEmitterId(username);
		SseEmitter sseEmitter = emitterRepository.save(emitterId, new SseEmitter(TIME_OUT_MILLISECOND));

		sseEmitter.onCompletion(() -> emitterRepository.delete(emitterId));
		sseEmitter.onTimeout(() -> emitterRepository.delete(emitterId));

		String eventId = generateEmitterId(username);
		sendNotification(sseEmitter, eventId, emitterId, "Connection established [username=" + username + "]");

		if (StringUtils.isNotEmpty(lastEventId)) {
			sendLostEvents(lastEventId, username, sseEmitter);
		}

		return sseEmitter;
	}

	@Override
	public void send(Object data) {
		Set<String> emitterKeys = redisTemplate.keys(EMITTER_KEY_PREFIX + "*");

		if (emitterKeys == null || emitterKeys.isEmpty()) {
			return;
		}

		emitterKeys.forEach(emitterKey -> {
			String username = emitterKey.replace(EMITTER_KEY_PREFIX, "");
			String eventId = generateEmitterId(username);

			emitterRepository.saveEvent(eventId, data);

			Map<String, SseEmitter> emitters = emitterRepository.findAllEmitters(username);
			emitters.forEach((emitterId, emitter) -> {
				sendNotification(emitter, eventId, emitterId, data);
			});
		});
	}

	@Override
	public void send(String username, Object data) {
		String eventId = generateEmitterId(username);

		emitterRepository.saveEvent(eventId, data);

		Map<String, SseEmitter> emitters = emitterRepository.findAllEmitters(username);
		emitters.forEach((emitterId, emitter) -> {
			sendNotification(emitter, eventId, emitterId, data);
		});
	}

	@Override
	public void save(Notify notify) {
		notifyRepository.save(notify);
	}

	@Override
	public void saves(List<Notify> notifies) {
		notifyRepository.saveAll(notifies);
	}

	private String generateEmitterId(String username) {
		return username + "_" + System.currentTimeMillis();
	}

	private void sendNotification(SseEmitter sseEmitter, String eventId, String emitterId, Object data) {
		try {
			sseEmitter.send(SseEmitter.event()
				.id(eventId)
				.name("notify")
				.data(data));
		} catch (IOException e) {
			emitterRepository.delete(emitterId);
		}
	}

	private void sendLostEvents(String lastEventId, String username, SseEmitter sseEmitter) {
		Map<String, Object> events = emitterRepository.findAllEvents(username);

		events.entrySet().stream()
			.filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
			.forEach(entry -> sendNotification(sseEmitter, entry.getKey(), null, entry.getValue()));
	}

	@Override
	public List<NotifyResponseDTO.Send> readAll(Authentication authentication, boolean read) {
		if (!authentication.isAuthenticated()) {
			throw NotifyError.UNAUTHORIZED_READ_ALL.getException();
		}

		Member member = memberService.getAuthenticatedMember(authentication);

		List<Notify> notifies = notifyRepository.findByMemberIdAndRead(member.getId(), read);

		if (notifies.isEmpty()) {
			throw NotifyError.NOT_FOUND_NOTIFIES.getException();
		}

		return notifies.stream().map(NotifyMapper::toDTO).toList();
	}

	@Override
	public void checkRead(Authentication authentication, Long notifyId) {
		if (!authentication.isAuthenticated()) {
			throw NotifyError.UNAUTHORIZED_CHECK.getException();
		}

		Member member = memberService.getAuthenticatedMember(authentication);

		Notify notify = notifyRepository.findByNotifyIdAndMemberId(notifyId, member.getId(), false)
			.orElseThrow(NotifyError.NOT_FOUND_NOTIFY_CHECK::getException);

		notify.check();

		notifyRepository.save(notify);
	}

	@Override
	public void checkRead(Authentication authentication) {
		if (!authentication.isAuthenticated()) {
			throw NotifyError.UNAUTHORIZED_CHECK.getException();
		}

		Member member = memberService.getAuthenticatedMember(authentication);

		List<Notify> notifies = notifyRepository.findByMemberIdAndRead(member.getId(), false);

		if (notifies.isEmpty()) {
			throw NotifyError.NOT_FOUND_NOTIFY_CHECKS.getException();
		}

		notifies.forEach(Notify::check);

		notifyRepository.saveAll(notifies);
	}
}
