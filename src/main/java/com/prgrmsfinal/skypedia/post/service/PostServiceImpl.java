package com.prgrmsfinal.skypedia.post.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.entity.Role;
import com.prgrmsfinal.skypedia.member.mapper.MemberMapper;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.notify.constant.NotifyType;
import com.prgrmsfinal.skypedia.notify.dto.NotifyRequestDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.photo.entity.Photo;
import com.prgrmsfinal.skypedia.photo.entity.PostPhoto;
import com.prgrmsfinal.skypedia.photo.entity.PostPhotoId;
import com.prgrmsfinal.skypedia.photo.repository.PhotoRepository;
import com.prgrmsfinal.skypedia.photo.repository.PostPhotoRepository;
import com.prgrmsfinal.skypedia.photo.service.PhotoService;
import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.post.entity.PostCategory;
import com.prgrmsfinal.skypedia.post.exception.PostError;
import com.prgrmsfinal.skypedia.post.repository.PostLikesRepository;
import com.prgrmsfinal.skypedia.post.repository.PostRepository;
import com.prgrmsfinal.skypedia.post.repository.PostScrapRepository;
import com.prgrmsfinal.skypedia.post.util.PostMapper;
import com.prgrmsfinal.skypedia.reply.dto.ReplyRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
	private final RedisTemplate<String, String> redisTemplate;

	private final PostRepository postRepository;

	private final PostLikesRepository postLikesRepository;

	private final PostScrapRepository postScrapRepository;

	private final PostCategoryService postCategoryService;

	private final PostReplyService postReplyService;

	private final MemberService memberService;

	private final ApplicationEventPublisher eventPublisher;

	private final PhotoService photoService;

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

	private final PhotoRepository photoRepository;

	private final PostPhotoRepository postPhotoRepository;

	@Override
	public PostResponseDTO.Read read(Authentication authentication, Long postId) {
		Long memberId = null;

		//회원검증
		if (authentication != null && authentication.isAuthenticated()) {
			memberId = memberService.getAuthenticatedMember(authentication).getId();
		}

		//게시물 유효성
		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		log.info("postId : {}", postId);
		incrementViewsCache(postId);

		MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(post.getMember());

		PostResponseDTO.Statistics postStats = PostResponseDTO.Statistics.builder()
			.views(getViews(postId))
			.likes(getLikes(postId))
			.liked(memberId != null && getLiked(memberId, postId))
			.scraped(memberId != null && getScraped(memberId, postId))
			.build();

		ReplyResponseDTO.ReadAll replies = postReplyService.readAll(authentication, postId, 0);

		List<PhotoResponseDTO.Info> photos = photoService.readPhotoUrlListByPostId(postId);

		return PostMapper.toDTO(post, memberInfo, postStats, photos, replies);
	}

	@Override
	public PostResponseDTO.ReadAll readAll(String order, String category, int page) {
		if (category != null && !postCategoryService.existsByName(category)) {
			throw PostError.NOT_FOUND_CATEGORY.getException();
		}

		Sort sort = null;

		if (StringUtils.isBlank(order)) {
			sort = Sort.by("id").descending();
		} else {
			sort = switch (order) {
				case "likes" -> Sort.by("likes").descending().and(Sort.by("id").descending());
				case "title" -> Sort.by("title").ascending().and(Sort.by("id").descending());
				case "rating" -> Sort.by("rating").descending().and(Sort.by("id").descending());
				default -> throw PostError.BAD_REQUEST_SORT_ORDER.getException();
			};
		}

		Pageable pageable = PageRequest.of(page, 10, sort);
		Slice<Post> result = (StringUtils.isBlank(category)) ? postRepository.findAllByDeleted(false, pageable)
			: postRepository.findAllByCategory(false, category, pageable);

		if (result == null || result.isEmpty()) {
			throw PostError.NOT_FOUND_POSTS.getException();
		}

		List<PostResponseDTO.Info> posts = makeResult(result);

		if (!result.hasNext()) {
			return new PostResponseDTO.ReadAll(posts, null);
		}

		Map<String, String> params = new HashMap<>() {{
			put("order", order);
			put("category", category);
		}};

		return new PostResponseDTO.ReadAll(posts, makeNextUri("/api/v1/posts?", params, page + 1));
	}

	@Override
	public PostResponseDTO.ReadAll readAll(String username, int page) {
		Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
		Slice<Post> result = postRepository.findAllByUsername(username, false, pageable);

		if (result == null || result.isEmpty()) {
			throw PostError.NOT_FOUND_POSTS.getException();
		}

		List<PostResponseDTO.Info> posts = makeResult(result);

		if (!result.hasNext()) {
			return new PostResponseDTO.ReadAll(posts, null);
		}

		return new PostResponseDTO.ReadAll(posts, makeNextUri("/api/v1/posts/" + username, null, page + 1));
	}

	@Override
	public PostResponseDTO.ReadAll readAll(Authentication authentication, int page) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_READ_SCRAPS.getException();
		}

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
		Slice<Post> result = postScrapRepository.findAllByScraped(memberId, false, pageable);

		if (result == null || result.isEmpty()) {
			throw PostError.NOT_FOUND_POSTS.getException();
		}

		List<PostResponseDTO.Info> posts = makeResult(result);

		if (!result.hasNext()) {
			return new PostResponseDTO.ReadAll(posts, null);
		}

		return new PostResponseDTO.ReadAll(posts, makeNextUri("/api/v1/posts/scrap", null, page + 1));
	}

	@Override
	public PostResponseDTO.ReadAll search(String keyword, String option, int page) {
		if (StringUtils.isBlank(option)) {
			throw PostError.BAD_REQUEST_SEARCH_OPTION.getException();
		}

		if (StringUtils.isBlank(keyword) || keyword.length() <= 2) {
			throw PostError.BAD_REQUEST_SEARCH_KEYWORD.getException();
		}

		int offset = page * 10;

		List<Post> result = switch (option) {
			case "title" -> postRepository.findPostsByTitleKeyword(keyword, offset);
			case "hashtags" -> postRepository.findPostsByHashtagsKeyword(keyword, offset);
			default -> throw PostError.BAD_REQUEST_SEARCH_OPTION.getException();
		};

		if (result == null || result.isEmpty()) {
			throw PostError.NOT_FOUND_POSTS.getException();
		}

		List<PostResponseDTO.Info> posts = makeResult(result);

		if (posts.size() != 10) {
			return new PostResponseDTO.ReadAll(posts, null);
		}

		Map<String, String> params = new HashMap<>() {{
			put("keyword", keyword);
			put("option", option);
		}};

		return new PostResponseDTO.ReadAll(posts, makeNextUri("/api/v1/posts/search?", params, page + 1));
	}

	private List<PostResponseDTO.Info> makeResult(Slice<Post> posts) {
		return posts.stream()
			.map(post -> {
				Long postId = post.getId();
				MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(post.getMember());
				Long replies = postReplyService.getReplyCount(post.getId());
				String photoUrl = photoService.readPhotoUrlByPostId(postId);
				return PostMapper.toDTO(post, memberInfo, getViews(postId),
					getLikes(postId), replies, photoUrl);
			}).toList();
	}

	private List<PostResponseDTO.Info> makeResult(List<Post> posts) {
		return posts.stream()
			.map(post -> {
				Long postId = post.getId();
				MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(post.getMember());
				Long replies = postReplyService.getReplyCount(post.getId());
				return PostMapper.toDTO(post, memberInfo, getViews(postId),
					getLikes(postId), replies, null);
			}).toList();
	}

	private String makeNextUri(String baseUri, Map<String, String> params, int page) {
		StringBuilder uri = new StringBuilder(baseUri)
			.append("page=").append(page).append("&");

		if (params == null || params.isEmpty()) {
			return uri.toString();
		}

		params.forEach((param, value) -> {
			if (StringUtils.isNotBlank(value)) {
				uri.append(param).append("=")
					.append(value).append("&");
			}
		});

		return uri.deleteCharAt(uri.length() - 1).toString();
	}

	@Override
	public ReplyResponseDTO.ReadAll readReplies(Authentication authentication, Long postId, int page) {
		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		return postReplyService.readAll(authentication, postId, page);
	}

	@Override
	public List<String> create(Authentication authentication, PostRequestDTO.Create request) {
		if (!authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_CREATE.getException();
		}

		PostCategory category = postCategoryService.getByName(request.getCategory())
			.orElseThrow(PostError.NOT_FOUND_CATEGORY::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		Post post = postRepository.save(Post.builder()
			.title(request.getTitle())
			.content(request.getContent())
			.category(category)
			.rating(request.getRating())
			.hashtags(String.join(",", request.getHashtags()))
			.member(member)
			.build());

		if (category.getName().equals("공지")) {
			eventPublisher.publishEvent(new NotifyRequestDTO.Global(
				"새로운 공지가 등록되었습니다.",
				NotifyType.NOTICE,
				"/api/v1/post/" + post.getId(),
				LocalDateTime.now()
			));
		}

		// 이미지 데이터가 없는 경우 null 반환
		if (request.getUploads() == null || request.getUploads().isEmpty()) {
			return null;
		}

		// PhotoService를 통해 사진 업로드 및 URL 받기
		List<PhotoResponseDTO.Info> photoInfos = photoService.createPhotoUrlList(request.getUploads());

		//photo 생성된거에서 id 끄집어오기
		List<Long> photoIds = photoInfos.stream()
			.map(PhotoResponseDTO.Info::getId)
			.collect(Collectors.toList());

		Map<Long, Photo> photoMap = photoRepository.findAllById(photoIds).stream()
			.collect(Collectors.toMap(Photo::getId, photo -> photo));

		for (Long photoId : photoIds) {
			Photo photo = photoMap.get(photoId);
			if (photo == null) {
				throw new RuntimeException("해당 ID의 사진을 찾을 수 없습니다: " + photoId);
			}

			// 이 밑에 수정 안함.
			// 복합 키 생성 아이디 생성.
			PostPhotoId postPhotoId = new PostPhotoId();
			postPhotoId.setPostId(post.getId());
			postPhotoId.setPhotoId(photoId);

			// 셀렉트 포토 생성.
			PostPhoto postPhoto = PostPhoto.builder()
				.id(postPhotoId)
				.post(post)
				.photo(photo)
				.build();

			postPhotoRepository.save(postPhoto);
		}
		List<String> photoUrls = photoInfos.stream()
			.map(PhotoResponseDTO.Info::getPhotoUrl)
			.collect(Collectors.toList());

		return photoUrls;
	}

	@Override
	public void createReply(Authentication authentication, Long postId, PostRequestDTO.CreateReply request) {
		if (!authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_CREATE_REPLY.getException();
		}

		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		postReplyService.create(post, new ReplyRequestDTO.Create(request.getParentId(), request.getContent(), member));
	}

	@Override
	public List<String> modify(Authentication authentication, Long postId, PostRequestDTO.Modify request) {
		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		if (!post.getMember().getId().equals(member.getId())) {
			throw PostError.UNAUTHORIZED_MODIFY.getException();
		}

		post.modify(request.getTitle(), request.getContent(), request.getHashtags());

		postRepository.save(post);

		// 사진 연동 작업이 필요함!!!
		return null;
	}

	@Override
	public void delete(Authentication authentication, Long postId) {
		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_PERMANENT::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		if (!member.getRole().equals(Role.ROLE_ADMIN) && !post.getMember().getId().equals(member.getId())) {
			throw PostError.UNAUTHORIZED_DELETE.getException();
		}

		post.delete();

		postRepository.save(post);
	}

	@Override
	public void restore(Authentication authentication, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(PostError.NOT_FOUND_PERMANENT::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		if (!member.getRole().equals(Role.ROLE_ADMIN) && member.getId() != post.getMember().getId()) {
			throw PostError.UNAUTHORIZED_RESTORE.getException();
		}

		if (!post.isDeleted()) {
			throw PostError.BAD_REQUEST_RESTORE.getException();
		}

		post.restore();

		postRepository.save(post);
	}

	@Override
	public PostResponseDTO.LikeStatus toggleLikes(Authentication authentication, Long postId) {
		if (!authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_TOGGLE_LIKES.getException();
		}

		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		String likesKey = POST_LIKES_PREFIX_KEY + postId;
		String unlikesKey = POST_UNLIKES_PREFIX_KEY + postId;
		boolean isLiked = getLiked(memberId, postId);

		if (!isLiked) {
			redisTemplate.opsForSet().add(likesKey, memberId.toString());
			redisTemplate.opsForSet().remove(unlikesKey, memberId.toString());
		} else {
			redisTemplate.opsForSet().add(unlikesKey, memberId.toString());
			redisTemplate.opsForSet().remove(likesKey, memberId.toString());
		}

		return new PostResponseDTO.LikeStatus(!isLiked, getLikes(postId));
	}

	private boolean getLiked(Long memberId, Long postId) {
		String likesKey = POST_LIKES_PREFIX_KEY + postId;
		String unlikesKey = POST_UNLIKES_PREFIX_KEY + postId;

		boolean isLiked = redisTemplate.opsForSet().isMember(likesKey, memberId.toString());
		boolean isUnliked = redisTemplate.opsForSet().isMember(unlikesKey, memberId.toString());

		if (!isLiked && !isUnliked) {
			return postLikesRepository.existsByPostIdAndMemberId(postId, memberId);
		}

		return isLiked && !isUnliked;
	}

	private Long getLikes(Long postId) {
		String likesKey = POST_LIKES_PREFIX_KEY + postId;
		String unlikesKey = POST_UNLIKES_PREFIX_KEY + postId;

		Long cachedLikes = redisTemplate.opsForSet().size(likesKey);
		Long cachedUnlikes = redisTemplate.opsForSet().size(unlikesKey);
		Long dbLikes = postRepository.findLikesById(postId);

		return dbLikes + (cachedLikes != null ? cachedLikes : 0) - (cachedUnlikes != null ? cachedUnlikes : 0);
	}

	@Override
	public boolean toggleScrap(Authentication authentication, Long postId) {
		if (!authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_TOGGLE_SCRAP.getException();
		}

		Long authorId = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException).getMember().getId();

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		if (authorId == memberId) {
			throw PostError.BAD_REQUEST_TOGGLE_SCRAP.getException();
		}

		String scrapKey = POST_SCRAP_PREFIX_KEY + postId;
		String unscrapKey = POST_UNSCRAP_PREFIX_KEY + postId;
		boolean isScraped = getScraped(memberId, postId);

		if (!isScraped) {
			redisTemplate.opsForSet().add(scrapKey, memberId.toString());
			redisTemplate.opsForSet().remove(unscrapKey, memberId.toString());
		} else {
			redisTemplate.opsForSet().add(unscrapKey, memberId.toString());
			redisTemplate.opsForSet().remove(scrapKey, memberId.toString());
		}

		return !isScraped;
	}

	private boolean getScraped(Long memberId, Long postId) {
		String scrapKey = POST_SCRAP_PREFIX_KEY + postId;
		String unscrapKey = POST_UNSCRAP_PREFIX_KEY + postId;

		boolean isScrap = redisTemplate.opsForSet().isMember(scrapKey, memberId.toString());
		boolean isUnscrap = redisTemplate.opsForSet().isMember(unscrapKey, memberId.toString());

		if (!isScrap && !isUnscrap) {
			return postScrapRepository.existsByPostIdAndMemberId(postId, memberId);
		}

		return isScrap && !isUnscrap;
	}

	private Long getViews(Long postId) {
		String cachedViewsStr = (String)redisTemplate.opsForHash().get(POST_VIEWS_PREFIX_KEY, postId.toString());
		Long cachedViews = cachedViewsStr != null ? Long.parseLong(cachedViewsStr) : 0L;
		Long dbViews = postRepository.findViewsById(postId);

		return dbViews + cachedViews;
	}

	private void incrementViewsCache(Long postId) {
		redisTemplate.opsForValue().increment(POST_VIEWS_PREFIX_KEY + postId, 1);
	}

	private Pageable getPageable(int size, Sort sort) {
		return PageRequest.of(1, size, sort);
	}
}
