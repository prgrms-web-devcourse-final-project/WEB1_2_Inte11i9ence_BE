package com.prgrmsfinal.skypedia.post.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

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
import com.prgrmsfinal.skypedia.post.entity.PostScrap;
import com.prgrmsfinal.skypedia.post.entity.key.PostLikesId;
import com.prgrmsfinal.skypedia.post.entity.key.PostScrapId;
import com.prgrmsfinal.skypedia.post.repository.PostLikesRepository;
import com.prgrmsfinal.skypedia.post.repository.PostRepository;
import com.prgrmsfinal.skypedia.post.repository.PostScrapRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostScheduler {
	private final RedisTemplate<String, String> redisTemplate;

	private final PostRepository postRepository;

	private final MemberRepository memberRepository;

	private final PostLikesRepository postLikesRepository;

	private final PostScrapRepository postScrapRepository;

	@Value("${post.views.prefix.key}")
	private String POST_VIEWS_PREFIX_KEY;

	@Value("${post.likes.prefix.key}")
	private String POST_LIKES_PREFIX_KEY;

	@Value("${post.unlikes.prefix.key}")
	private String POST_UNLIKES_PREFIX_KEY;

	@Value("${post.scrap.prefix.key}")
	private String POST_SCRAP_PREFIX_KEY;

	@Value("${post.unscrap.prefix.key}")
	private String POST_UNSCRAP_PREFIX_KEY;

	@Scheduled(fixedRate = 10000)
	public void syncScrapCacheToDB() {
		ScanOptions scrapScanOptions = ScanOptions.scanOptions().match(POST_SCRAP_PREFIX_KEY + "*").build();
		ScanOptions unscrapScanOptions = ScanOptions.scanOptions().match(POST_UNSCRAP_PREFIX_KEY + "*").build();

		Cursor<byte[]> scrapCursor = redisTemplate.executeWithStickyConnection(
			connection -> connection.scan(scrapScanOptions)
		);
		Cursor<byte[]> unscrapCursor = redisTemplate.executeWithStickyConnection(
			connection -> connection.scan(unscrapScanOptions)
		);

		processScrapCursor(scrapCursor, POST_SCRAP_PREFIX_KEY, true);

		processScrapCursor(unscrapCursor, POST_UNSCRAP_PREFIX_KEY, false);
	}

	private void processScrapCursor(Cursor<byte[]> cursor, String prefixKey, boolean isScrap) {
		while (cursor != null && cursor.hasNext()) {
			String key = new String(cursor.next(), StandardCharsets.UTF_8);
			Long postId = Long.parseLong(key.replace(prefixKey, ""));

			try {
				if (isScrap) {
					syncScrapsToDB(postId);
				} else {
					syncUnscrapsToDB(postId);
				}
			} catch (Exception e) {
				log.warn("Error syncing scraps for postId {}: {}", postId, e.getMessage());
			}
		}
	}

	@Transactional
	public void syncScrapsToDB(Long postId) {
		String scrapKey = POST_SCRAP_PREFIX_KEY + postId;
		Set<String> values = redisTemplate.opsForSet().members(scrapKey);

		if (values == null || values.isEmpty()) {
			return;
		}

		List<PostScrap> postScraps = new ArrayList<>();

		for (String value : values) {
			Long memberId = Long.parseLong(value);

			try {
				if (postScrapRepository.existsByPostIdAndMemberId(postId, memberId)) {
					log.warn("Duplicate scrap entry for postId {} and memberId {}", postId, memberId);
					continue;
				}

				Member member = memberRepository.findById(memberId).orElseThrow(() ->
					new NoSuchElementException("Member not found: " + memberId));

				Post post = postRepository.findById(postId).orElseThrow(() ->
					new NoSuchElementException("Post not found: " + postId));

				postScraps.add(PostScrap.builder()
					.id(new PostScrapId(postId, memberId))
					.post(post)
					.member(member)
					.build());
			} catch (NoSuchElementException e) {
				log.warn("Skipping invalid scrap entry for postId {} and memberId {}: {}", postId, memberId, e.getMessage());
			}
		}

		postScrapRepository.saveAll(postScraps);
		redisTemplate.delete(scrapKey);
	}

	@Transactional
	public void syncUnscrapsToDB(Long postId) {
		String unscrapKey = POST_UNSCRAP_PREFIX_KEY + postId;
		Set<String> values = redisTemplate.opsForSet().members(unscrapKey);

		if (values == null || values.isEmpty()) {
			return;
		}

		for (String value : values) {
			Long memberId = Long.parseLong(value);

			try {
				PostScrap postScrap = postScrapRepository.findByPostIdAndMemberId(postId, memberId).orElseThrow(() ->
					new NoSuchElementException("No scrap entry found for postId " + postId + " and memberId " + memberId));

				postScrapRepository.delete(postScrap);
			} catch (NoSuchElementException e) {
				log.warn("Skipping invalid unscrap entry for postId {} and memberId {}: {}", postId, memberId, e.getMessage());
			}
		}

		redisTemplate.delete(unscrapKey);
	}

	@Scheduled(fixedRate = 10000)
	public void syncViews() {
		ScanOptions scanOptions = ScanOptions.scanOptions().match(POST_VIEWS_PREFIX_KEY + "*").build();

		Cursor<byte[]> cursors = redisTemplate.executeWithStickyConnection(
			connection -> connection.scan(scanOptions)
		);

		Map<Long, Long> viewsMap = new HashMap<>();

		while (cursors != null && cursors.hasNext()) {
			String key = new String(cursors.next(), StandardCharsets.UTF_8);
			Long postId = Long.parseLong(key.replace(POST_VIEWS_PREFIX_KEY, ""));
			Long views = Long.parseLong(redisTemplate.opsForValue().get(key).toString());

			viewsMap.put(postId, views);
		}

		List<Long> failedIds = syncViewsToDB(viewsMap);

		for (Long failedId : failedIds) {
			log.warn("Error Syncing likes for postId {}", failedId);
		}

		viewsMap.keySet().forEach(postId -> redisTemplate.delete(POST_VIEWS_PREFIX_KEY + postId));
	}

	@Transactional
	public List<Long> syncViewsToDB(Map<Long, Long> viewsMap) {
		List<Long> failedIds = new ArrayList<>();

		viewsMap.forEach((postId, views) -> {
			int updated = postRepository.updateViewsCount(postId, views);
			if (updated == 0) {
				failedIds.add(postId);
			}
		});

		return failedIds;
	}

	@Scheduled(fixedRate = 10000)
	public void syncLikes() {
		ScanOptions likesScanOptions = ScanOptions.scanOptions().match(POST_LIKES_PREFIX_KEY + "*").build();
		ScanOptions unlikesScanOptions = ScanOptions.scanOptions().match(POST_UNLIKES_PREFIX_KEY + "*").build();

		Cursor<byte[]> likeCursors = redisTemplate.executeWithStickyConnection(
			connection -> connection.scan(likesScanOptions)
		);

		Cursor<byte[]> unlikeCursors = redisTemplate.executeWithStickyConnection(
			connection -> connection.scan(unlikesScanOptions)
		);

		processLikesCursor(likeCursors, POST_LIKES_PREFIX_KEY);

		processLikesCursor(unlikeCursors, POST_UNLIKES_PREFIX_KEY);
	}

	private void processLikesCursor(Cursor<byte[]> cursors, String prefixKey) {
		while (cursors != null && cursors.hasNext()) {
			String key = new String(cursors.next(), StandardCharsets.UTF_8);
			Long postId = Long.parseLong(key.replace(prefixKey, ""));

			try {
				syncLikesToDB(postId);
			} catch (Exception e) {
				log.warn("Error Syncing likes for postId {}: {}", postId, e.getMessage());
			}
		}
	}

	@Transactional
	public void syncLikesToDB(Long postId) {
		String likesKey = POST_LIKES_PREFIX_KEY + postId;
		String unlikesKey = POST_UNLIKES_PREFIX_KEY + postId;

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
			postLikesRepository.deleteByPostIdAndMemberId(postId, memberId);
		}

		Set<Long> likedMemberIdsInDB = postLikesRepository.findMemberIdsByPostId(postId);
		Long likesCount = 0L;

		for (Long memberId : likedMemberIds) {
			if (likedMemberIdsInDB.contains(memberId)) {
				log.warn("Duplicate like found for postId {} and memberId {}", postId, memberId);
			}

			try {
				Member member = memberRepository.findById(memberId).orElseThrow(() ->
					new NoSuchElementException("Member not found: " + memberId));

				Post post = postRepository.findById(postId).orElseThrow(() ->
					new NoSuchElementException("Post not found: " + postId));

				postLikesRepository.save(PostLikes.builder()
					.id(new PostLikesId(postId, memberId))
					.post(post)
					.member(member)
					.build());

				likesCount++;
			} catch (NoSuchElementException e) {
				log.warn("Skipping invalid like entry for postId {} and memberId {}: {}", postId, memberId, e.getMessage());
			}
		}

		redisTemplate.delete(likesKey);
		redisTemplate.delete(unlikesKey);

		postRepository.updateLikesCount(postId, likesCount - unlikedMemberIds.size());
	}
}
