package com.prgrmsfinal.skypedia.reply.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import com.prgrmsfinal.skypedia.reply.entity.ReplyLikes;
import com.prgrmsfinal.skypedia.reply.repository.ReplyLikesRepository;
import com.prgrmsfinal.skypedia.reply.repository.ReplyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReplyScheduler {
	private final RedisTemplate<String, String> redisTemplate;

	private final MemberRepository memberRepository;

	private final ReplyRepository replyRepository;

	private final ReplyLikesRepository replyLikesRepository;

	@Value("${reply.likes.prefix.key}")
	private String REPLY_LIKES_PREFIX_KEY;

	@Value("${reply.unlikes.prefix.key}")
	private String REPLY_UNLIKES_PREFIX_KEY;

	@Transactional
	@Scheduled(fixedRate = 10000)
	public void syncLikesCacheToDB() {
		Set<String> likesKeys = redisTemplate.keys(REPLY_LIKES_PREFIX_KEY + "*");
		Set<String> unlikesKeys = redisTemplate.keys(REPLY_UNLIKES_PREFIX_KEY + "*");

		if (likesKeys != null) {
			likesKeys.forEach(key -> {
				Long replyId = Long.parseLong(key.replace(REPLY_LIKES_PREFIX_KEY, ""));
				Set<String> values = redisTemplate.opsForSet().members(key);
				List<ReplyLikes> replyLikes = new ArrayList<>();

				values.forEach(value -> {
					Long memberId = Long.parseLong(value);

					try {
						if (replyLikesRepository.existsByReplyIdAndMemberId(replyId, memberId)) {
							throw new IllegalArgumentException(StringUtils.join(
								"data already exists. [replyId=", replyId, ", memberId=", memberId, "]"));
						}

						Member member = memberRepository.findById(memberId).orElseThrow(()
							-> new NoSuchElementException(
							StringUtils.join("invalid member id. [memberId=", memberId, "]")));

						Reply reply = replyRepository.findById(replyId).orElseThrow(()
							-> new NoSuchElementException(
							StringUtils.join("invalid reply id. [replyId=", replyId, "]")));

						replyLikes.add(ReplyLikes.builder()
							.member(member)
							.reply(reply)
							.build());
					} catch (IllegalArgumentException | NoSuchElementException e) {
						log.warn("DB Sync error: {}", e.getMessage());
					}
				});
				redisTemplate.delete(REPLY_LIKES_PREFIX_KEY + replyId);
				replyLikesRepository.saveAll(replyLikes);
			});
		}

		if (unlikesKeys != null) {
			unlikesKeys.forEach(key -> {
				Long replyId = Long.parseLong(key.replace(REPLY_UNLIKES_PREFIX_KEY, ""));
				Set<String> values = redisTemplate.opsForSet().members(key);

				values.forEach(value -> {
					Long memberId = Long.parseLong(value);

					try {
						if (!replyLikesRepository.existsByReplyIdAndMemberId(replyId, memberId)) {
							throw new IllegalArgumentException(StringUtils.join(
								"data not exists. [replyId=", replyId, ", memberId=", memberId, "]"
							));
						}

						replyLikesRepository.delete(replyLikesRepository.findByReplyIdAndMemberId(replyId, memberId)
							.orElseThrow(() -> new NoSuchElementException(StringUtils.join(
								"invalid memberId or postId. [replyId=", replyId, ", memberId=", memberId, "]")
							)));
					} catch (IllegalArgumentException | NoSuchElementException e) {
						log.warn("DB Sync error: {}", e.getMessage());
					}
				});

				redisTemplate.delete(StringUtils.join(REPLY_UNLIKES_PREFIX_KEY, replyId));
			});
		}
	}
}
