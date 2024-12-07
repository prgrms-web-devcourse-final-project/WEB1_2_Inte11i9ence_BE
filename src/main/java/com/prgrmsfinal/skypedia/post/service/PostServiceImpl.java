package com.prgrmsfinal.skypedia.post.service;

import java.util.List;

import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.mapper.MemberMapper;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.notify.constant.NotifyType;
import com.prgrmsfinal.skypedia.notify.dto.NotifyRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.post.entity.PostCategory;
import com.prgrmsfinal.skypedia.post.exception.PostError;
import com.prgrmsfinal.skypedia.post.repository.PostLikesRepository;
import com.prgrmsfinal.skypedia.post.repository.PostRepository;
import com.prgrmsfinal.skypedia.post.repository.PostScrapRepository;
import com.prgrmsfinal.skypedia.post.util.PostMapper;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import com.prgrmsfinal.skypedia.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;

// ※ 추후 검색 기능 구현 및 로깅 작업 필요함.

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final RedisTemplate<String, String> redisTemplate;

	private final MemberRepository memberRepository;

	private final PostRepository postRepository;

	private final PostLikesRepository postLikesRepository;

	private final PostScrapRepository postScrapRepository;

	private final PostCategoryService postCategoryService;

	private final PostReplyService postReplyService;

	private final MemberService memberService;

	private final ReplyService replyService;

	private final ApplicationEventPublisher eventPublisher;

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

	@Override
	public PostResponseDTO.Read read(Authentication authentication, Long postId) {
		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		incrementViews(postId);

		MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(post.getMember());

		PostResponseDTO.Statistics postStats = PostResponseDTO.Statistics.builder()
			.views(getViews(postId, post.getViews()))
			.likes(getLikes(postId, post.getLikes()))
			.liked(isCurrentMemberLiked(authentication, post))
			.scraped(isCurrentMemberScraped(authentication, post))
			.build();

		ReplyResponseDTO.ReadAll replies = postReplyService.readAll(authentication, postId, 0L);

		return PostMapper.toDTO(post, memberInfo, postStats, null, replies);
	}

	@Override
	public PostResponseDTO.ReadAll readAll(String category, String cursor, Long lastId, String order) {
		if (!postCategoryService.existsByName(category)) {
			throw PostError.NOT_FOUND_CATEGORY.getException();
		}

		List<Post> posts = null;

		if (StringUtils.isBlank(order)) {
			posts = postRepository.findPostsById(lastId, false, category,
				getPageable(10, Sort.by(Sort.Direction.DESC, "id")));
		} else {
			posts = switch (order) {
				case "likes" -> postRepository.findPostsByLikes(Long.parseLong(cursor), lastId, false, category,
					getPageable(10, Sort.by(Sort.Direction.DESC, "likes").and(Sort.by(Sort.Direction.DESC, "id"))));
				case "title" -> postRepository.findPostsByTitle(cursor, lastId, false, category,
					getPageable(10, Sort.by(Sort.Direction.ASC, "title").and(Sort.by(Sort.Direction.DESC, "id"))));
				case "rating" -> postRepository.findPostsByRating(Float.parseFloat(cursor), lastId, false, category,
					getPageable(10, Sort.by(Sort.Direction.DESC, "rating").and(Sort.by(Sort.Direction.DESC, "id"))));
				default -> throw PostError.BAD_REQUEST_SORT_ORDER.getException();
			};
		}

		if (posts == null || posts.isEmpty()) {
			throw PostError.NOT_FOUND_POSTS.getException();
		}

		List<PostResponseDTO.Info> response = posts.stream()
			.map(post -> {
				MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(post.getMember());
				Long replies = postReplyService.getReplyCount(post.getId());
				return PostMapper.toDTO(post, memberInfo, getViews(post.getId(), post.getViews()),
					getLikes(post.getId(), post.getLikes()), replies, null);
			}).toList();

		Post lastPost = posts.get(posts.size() - 1);
		StringBuilder nextUri = new StringBuilder("/api/v1/posts?lastId=" + lastPost.getId());

		nextUri.append(switch (order) {
			case "likes" -> "&cursor=" + lastPost.getLikes();
			case "title" -> "&cursor=" + lastPost.getTitle();
			case "rating" -> "&cursor=" + lastPost.getRating();
			default -> "";
		});

		if (category != null) {
			nextUri.append("&category=" + category);
		}

		return new PostResponseDTO.ReadAll(response, nextUri.toString());
	}

	@Override
	public PostResponseDTO.ReadAll readAll(String username, Long lastId) {
		if (!memberService.checkExistsByUsername(username)) {
			throw PostError.NOT_FOUND_USERNAME.getException();
		}

		List<Post> posts = postRepository.findPostsByUsername(username, lastId, false,
			getPageable(10, Sort.by(Sort.Direction.DESC, "id")));

		if (posts == null || posts.isEmpty()) {
			throw PostError.NOT_FOUND_POSTS.getException();
		}

		List<PostResponseDTO.Info> response = posts.stream()
			.map(post -> {
				MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(post.getMember());
				Long replies = postReplyService.getReplyCount(post.getId());
				return PostMapper.toDTO(post, memberInfo, getViews(post.getId(), post.getViews()),
					getLikes(post.getId(), post.getLikes()), replies, null);
			}).toList();

		Post lastPost = posts.get(posts.size() - 1);
		String nextUri = new StringBuilder()
			.append("/api/v1/posts/").append(username)
			.append("?lastId=").append(lastPost.getId()).toString();

		return new PostResponseDTO.ReadAll(response, nextUri);
	}

	@Override
	public PostResponseDTO.ReadAll readAll(Authentication authentication, Long lastId) {
		if (!authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_READ_SCRAPS.getException();
		}

		String username = memberService.getAuthenticatedMember(authentication).getUsername();

		List<Post> posts = postRepository.findPostsByUsername(username, lastId, false,
			getPageable(10, Sort.by(Sort.Direction.DESC, "id")));

		if (posts == null || posts.isEmpty()) {
			throw PostError.NOT_FOUND_POSTS.getException();
		}

		List<PostResponseDTO.Info> response = posts.stream()
			.map(post -> {
				MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(post.getMember());
				Long replies = postReplyService.getReplyCount(post.getId());
				return PostMapper.toDTO(post, memberInfo, getViews(post.getId(), post.getViews()),
					getLikes(post.getId(), post.getLikes()), replies, null);
			}).toList();

		Post lastPost = posts.get(posts.size() - 1);
		String nextUri = new StringBuilder()
			.append("/api/v1/posts/scrap")
			.append("&lastId=").append(lastPost.getId()).toString();

		return new PostResponseDTO.ReadAll(response, nextUri);
	}

	@Override
	public PostResponseDTO.ReadAll search(String keyword, String target, String cursor, Long lastId) {
		List<PostResponseDTO.Search> results = switch (target) {
			case "title" -> postRepository.findPostsByTitleKeyword(keyword, Double.parseDouble(cursor), lastId);
			case "hashtags" -> postRepository.findPostsByHashtagsKeyword(keyword, Double.parseDouble(cursor), lastId);
			default -> throw PostError.BAD_REQUEST_SEARCH_TARGET.getException();
		};

		if (results == null || results.isEmpty()) {
			throw PostError.NOT_FOUND_POSTS.getException();
		}

		List<PostResponseDTO.Info> response = results.stream()
			.map(post -> {
				MemberResponseDTO.Info memberInfo = MemberMapper.toDTO(post.getMember());
				Long replies = postReplyService.getReplyCount(post.getId());
				return PostMapper.toDTO(post, memberInfo, getViews(post.getId(), post.getViews()),
					getLikes(post.getId(), post.getLikes()), replies, null);
			}).toList();

		PostResponseDTO.Search lastResult = results.get(results.size() - 1);

		String nextUri = new StringBuilder()
			.append("/api/v1/posts?keyword=").append(keyword)
			.append("&target=").append(target)
			.append("&lastrev=").append(lastResult.getRelevance())
			.append("&lastidx=").append(lastResult.getId()).toString();

		return new PostResponseDTO.ReadAll(response, nextUri);
	}

	@Override
	public ReplyResponseDTO.ReadAll readReplies(Authentication authentication, Long postId, Long lastReplyId) {
		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		return postReplyService.readAll(authentication, postId, lastReplyId);
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
			eventPublisher.publishEvent(NotifyRequestDTO.Global.builder()
				.notifyType(NotifyType.NOTICE)
				.content("새로운 공지가 등록되었습니다.")
				.uri("/api/v1/post/" + post.getId())
				.build());
		}

		// 사진 연동 작업이 필요함!!!
		return null;
	}
//@Override
//public List<String> create(Long memberId, PostRequestDTO.Create request) {
//	Member member = memberRepository.findById(memberId)
//			.orElseThrow(() -> new UsernameNotFoundException("Member not found"));
//
//	PostCategory category = postCategoryService.getByName(request.getCategory())
//			.orElseThrow(PostError.NOT_FOUND_CATEGORY::getException);
//
//	Post post = postRepository.save(Post.builder()
//			.title(request.getTitle())
//			.content(request.getContent())
//			.category(category)
//			.rating(request.getRating())
//			.hashtags(String.join(",", request.getHashtags()))
//			.member(member)
//			.build());
//
//	if (category.getName().equals("공지")) {
//		eventPublisher.publishEvent(NotifyRequestDTO.Global.builder()
//				.notifyType(NotifyType.NOTICE)
//				.content("새로운 공지가 등록되었습니다.")
//				.uri("/api/v1/post/" + post.getId())
//				.build());
//	}
//
//	// 사진 연동 작업이 필요함!!!
//	return null;
//}

	@Override
	public void createReply(Authentication authentication, Long postId, PostRequestDTO.CreateReply request) {
		if (!authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_CREATE_REPLY.getException();
		}

		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		Member member = memberService.getAuthenticatedMember(authentication);

		Reply reply = replyService.create(request, member);

		postReplyService.create(post, reply);
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

		if (!post.getMember().getId().equals(member.getId())) {
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

		if (!member.getRole().equals("ROLE_ADMIN")) {
			throw PostError.UNAUTHORIZED_RESTORE.getException();
		}

		if (!post.isDeleted()) {
			throw PostError.BAD_REQUEST_RESTORE.getException();
		}

		post.restore();

		postRepository.save(post);
	}

	@Override
	public PostResponseDTO.ToggleLikes toggleLikes(Authentication authentication, Long postId) {
		if (!authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_TOGGLE_LIKES.getException();
		}

		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		String likesKey = POST_LIKES_PREFIX_KEY + postId;
		String unlikesKey = POST_UNLIKES_PREFIX_KEY + postId;
		boolean isLiked = isCurrentMemberLiked(authentication, post);

		if (!isLiked) {
			redisTemplate.opsForSet().add(likesKey, memberId.toString());
			redisTemplate.opsForSet().remove(unlikesKey, memberId.toString());
		} else {
			redisTemplate.opsForSet().add(unlikesKey, memberId.toString());
			redisTemplate.opsForSet().remove(likesKey, memberId.toString());
		}

		return new PostResponseDTO.ToggleLikes(!isLiked, getLikes(post.getId(), post.getLikes()));
	}

	@Override
	public boolean toggleScrap(Authentication authentication, Long postId) {
		if (!authentication.isAuthenticated()) {
			throw PostError.UNAUTHORIZED_TOGGLE_SCRAP.getException();
		}

		Post post = postRepository.findByIdAndDeleted(postId, false)
			.orElseThrow(PostError.NOT_FOUND_POST::getException);

		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		String scrapKey = POST_SCRAP_PREFIX_KEY + postId;
		String unscrapKey = POST_UNSCRAP_PREFIX_KEY + postId;
		boolean isScraped = isCurrentMemberScraped(authentication, post);

		if (!isScraped) {
			redisTemplate.opsForSet().add(scrapKey, memberId.toString());
			redisTemplate.opsForSet().remove(unscrapKey, memberId.toString());
		} else {
			redisTemplate.opsForSet().add(unscrapKey, memberId.toString());
			redisTemplate.opsForSet().remove(scrapKey, memberId.toString());
		}

		return !isScraped;
	}

	private Long getLikes(Long postId, Long likes) {
		String likesKey = StringUtils.join(POST_LIKES_PREFIX_KEY, postId);
		String unlikesKey = StringUtils.join(POST_UNLIKES_PREFIX_KEY, postId);

		if (Boolean.TRUE.equals(redisTemplate.hasKey(likesKey))) {
			likes += redisTemplate.opsForSet().size(likesKey);
		}

		if (Boolean.TRUE.equals(redisTemplate.hasKey(unlikesKey))) {
			likes -= redisTemplate.opsForSet().size(unlikesKey);
		}

		return likes;
	}

	private Long getViews(Long postId, Long views) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(StringUtils.join(POST_VIEWS_PREFIX_KEY, postId)))) {
			views += (Long)redisTemplate.opsForHash().get(POST_VIEWS_PREFIX_KEY, postId);
		}

		return views;
	}

	private void incrementViews(Long postId) {
		redisTemplate.opsForHash().increment(POST_VIEWS_PREFIX_KEY, postId.toString(), 1);
	}

	private boolean isCurrentMemberLiked(Authentication authentication, Post post) {
		String likesKey = StringUtils.join(POST_LIKES_PREFIX_KEY, post.getId());
		String unlikesKey = StringUtils.join(POST_UNLIKES_PREFIX_KEY, post.getId());
		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likesKey, memberId))) {
			return true;
		}

		if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(unlikesKey, memberId))) {
			return false;
		}

		if (postLikesRepository.existsByPostIdAndMemberId(post.getId(), memberId)) {
			return true;
		}

		return false;
	}

	private boolean isCurrentMemberScraped(Authentication authentication, Post post) {
		String scrapKey = POST_SCRAP_PREFIX_KEY + post.getId();
		String unscrapKey = POST_UNLIKES_PREFIX_KEY + post.getId();
		Long memberId = memberService.getAuthenticatedMember(authentication).getId();

		if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(scrapKey, memberId))) {
			return true;
		}

		if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(unscrapKey, memberId))) {
			return false;
		}

		if (postScrapRepository.existsByPostIdAndMemberId(post.getId(), memberId)) {
			return true;
		}

		return false;
	}

	private Pageable getPageable(int size, Sort sort) {
		return PageRequest.of(0, size, sort);
	}
}
