package com.prgrmsfinal.skypedia.reply.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.post.entity.PostLikes;
import com.prgrmsfinal.skypedia.post.entity.key.PostLikesId;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import com.prgrmsfinal.skypedia.reply.entity.ReplyLikes;
import com.prgrmsfinal.skypedia.reply.entity.key.ReplyLikesId;
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

	@Scheduled(fixedRate = 10000)
	public void syncLikes() {
		ScanOptions likesScanOptions = ScanOptions.scanOptions().match(REPLY_LIKES_PREFIX_KEY + "*").build();
		ScanOptions unlikesScanOptions = ScanOptions.scanOptions().match(REPLY_UNLIKES_PREFIX_KEY + "*").build();

		Cursor<byte[]> likeCursors = redisTemplate.executeWithStickyConnection(
			connection -> connection.scan(likesScanOptions)
		);

		Cursor<byte[]> unlikeCursors = redisTemplate.executeWithStickyConnection(
			connection -> connection.scan(unlikesScanOptions)
		);

		processLikesCursor(likeCursors, REPLY_LIKES_PREFIX_KEY);

		processLikesCursor(unlikeCursors, REPLY_UNLIKES_PREFIX_KEY);
	}

	private void processLikesCursor(Cursor<byte[]> cursors, String prefixKey) {
		while (cursors != null && cursors.hasNext()) {
			String key = new String(cursors.next(), StandardCharsets.UTF_8);
			Long replyId = Long.parseLong(key.replace(prefixKey, ""));

			try {
				syncLikesToDB(replyId);
			} catch (Exception e) {
				log.warn("Error Syncing likes for replyId {}: {}", replyId, e.getMessage());
			}
		}
	}

	@Transactional
	public void syncLikesToDB(Long replyId) {
		String likesKey = REPLY_LIKES_PREFIX_KEY + replyId;
		String unlikesKey = REPLY_UNLIKES_PREFIX_KEY + replyId;

		Set<String> likesValues = redisTemplate.opsForSet().members(likesKey);
		Set<String> unlikesValues = redisTemplate.opsForSet().members(unlikesKey);

		if ((likesValues == null || likesValues.isEmpty()) && (unlikesValues == null || unlikesValues.isEmpty())) {
			return;
		}

		List<Long> likedMemberIds = likesValues != null
			? likesValues.stream().map(Long::parseLong).toList() : List.of();
		List<Long> unlikedMemberIds = unlikesValues != null
			? unlikesValues.stream().map(Long::parseLong).toList() : List.of();

		for (Long memberId : unlikedMemberIds) {
			replyLikesRepository.deleteByReplyIdAndMemberId(replyId, memberId);
		}

		Set<Long> likedMemberIdsInDB = replyLikesRepository.findMemberIdsByReplyId(replyId);
		Long likesCount = 0L;

		for (Long memberId : likedMemberIds) {
			if (likedMemberIdsInDB.contains(memberId)) {
				log.warn("Duplicate like found for replyId {} and memberId {}", replyId, memberId);
			}

			try {
				Member member = memberRepository.findById(memberId).orElseThrow(() ->
					new NoSuchElementException("Member not found: " + memberId));

				Reply reply = replyRepository.findById(replyId).orElseThrow(() ->
					new NoSuchElementException("Reply not found: " + replyId));

				replyLikesRepository.save(ReplyLikes.builder()
					.id(new ReplyLikesId(replyId, memberId))
					.reply(reply)
					.member(member)
					.build());

				likesCount++;
			} catch (NoSuchElementException e) {
				log.warn("Skipping invalid like entry for replyId {} and memberId {}: {}", replyId, memberId, e.getMessage());
			}
		}

		redisTemplate.delete(likesKey);
		redisTemplate.delete(unlikesKey);

		replyRepository.updateLikesCount(replyId, likesCount - unlikedMemberIds.size());
	}
}
