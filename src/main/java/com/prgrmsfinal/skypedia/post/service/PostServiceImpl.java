package com.prgrmsfinal.skypedia.post.service;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.service.MemberService;
import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.post.entity.PostCategory;
import com.prgrmsfinal.skypedia.post.exception.PostError;
import com.prgrmsfinal.skypedia.post.repository.PostLikesRepository;
import com.prgrmsfinal.skypedia.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ※ 추후 검색 기능 구현 및 로깅 작업 필요함.

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final RedisTemplate<String, String> redisTemplate;

    private final PostRepository postRepository;

    private final PostLikesRepository postLikesRepository;

    private final PostCategoryService postCategoryService;

    private final MemberService memberService;

    @Value("${post.views.prefix.key}")
    private String POST_VIEWS_PREFIX_KEY;

    @Value("${post.likes.prefix.key}")
    private String POST_LIKES_PREFIX_KEY;

    @Value("${post.unlikes.prefix.key}")
    private String POST_UNLIKES_PREFIX_KEY;

    @Override
    public PostResponseDTO.Read read(Authentication authentication, Long postId) {
        Post post = postRepository.findByIdAndDeleted(postId, false)
                .orElseThrow(PostError.CANNOT_FOUND_POST::getException);

        incrementViews(postId);

        PostResponseDTO.Read response = PostResponseDTO.Read.byEntity(post);

        response.setViewsAndLikes(getViews(post), getLikes(post), isCurrentMemberLiked(authentication, post));

        return response;
    }

    @Override
    public List<PostResponseDTO.Read> readAll(String category, Long lastId, String order) {
        if (!postCategoryService.existsByName(category)) {
            throw PostError.CANNOT_FOUND_CATEGORY.getException();
        }

        List<Post> posts = null;

        if (StringUtils.isNotBlank(category)) {
            if (StringUtils.isBlank(order)) {
                posts = postRepository.findRecentPostsAfterId(lastId, false, category);
            } else {
                switch (order) {
                    case "views":
                        posts = postRepository.findPostsByViews(lastId, false, category);
                        break;
                    case "likes":
                        posts = postRepository.findPostsByLikes(lastId, false, category);
                        break;
                    case "title":
                        posts = postRepository.findPostsByTitle(lastId, false, category);
                        break;
                    case "rating":
                        posts = postRepository.findPostsByRating(lastId, false, category);
                        break;
                    default:
                        throw PostError.INVALID_SORT_ORDER.getException();
                }
            }
        }

        if (posts == null || posts.isEmpty()) {
            throw PostError.CANNOT_FOUND_POSTS.getException();
        }

        return posts.stream()
                .map(PostResponseDTO.Read::byEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void create(Authentication authentication, PostRequestDTO.Create request) {
        PostCategory category = postCategoryService.getByName(request.getCategory())
                .orElseThrow(PostError.CANNOT_FOUND_CATEGORY::getException);

        Member member = memberService.getAuthenticatedMember(authentication);

        postRepository.save(Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .rating(request.getRate())
                .member(member)
                .build());
    }

    @Override
    public void modify(Authentication authentication, Long postId, PostRequestDTO.Modify request) {
        Post post = postRepository.findByIdAndDeleted(postId, false)
                .orElseThrow(PostError.CANNOT_FOUND_POST::getException);

        Member member = memberService.getAuthenticatedMember(authentication);

        if (!post.getMember().getId().equals(member.getId())) {
            throw PostError.BAD_REQUEST_POST_MODIFY.getException();
        }

        post.modify(request.getTitle(), request.getContent());

        postRepository.save(post);
    }

    @Override
    public void delete(Authentication authentication, Long postId) {
        Post post = postRepository.findByIdAndDeleted(postId, false)
                .orElseThrow(PostError.CANNOT_FOUND_POST::getException);

        Member member = memberService.getAuthenticatedMember(authentication);

        if (!post.getMember().getId().equals(member.getId())) {
            throw PostError.BAD_REQUEST_POST_DELETE.getException();
        }

        post.delete();

        postRepository.save(post);
    }

    @Override
    public void restore(Authentication authentication, Long postId) {
        Post post = postRepository.findByIdAndDeleted(postId, true)
                .orElseThrow(PostError.CANNOT_FOUND_POST::getException);

        Member member = memberService.getAuthenticatedMember(authentication);

        if (!member.getRole().equals("ROLE_ADMIN")) {
            throw PostError.BAD_REQUEST_POST_RESTORE.getException();
        }

        post.restore();

        postRepository.save(post);
    }

    @Override
    public boolean toggleLikes(Authentication authentication, Long postId) {
        Post post = postRepository.findByIdAndDeleted(postId, false)
                .orElseThrow(PostError.CANNOT_FOUND_POST::getException);

        Long memberId = memberService.getAuthenticatedMember(authentication).getId();

        String likesKey = StringUtils.join(POST_LIKES_PREFIX_KEY, postId);
        String unlikesKey = StringUtils.join(POST_UNLIKES_PREFIX_KEY, postId);
        boolean isLiked = isCurrentMemberLiked(authentication, post);

        if (!isLiked) {
            redisTemplate.opsForSet().add(likesKey, memberId.toString());
            redisTemplate.opsForSet().remove(unlikesKey, memberId.toString());
        } else {
            redisTemplate.opsForSet().add(unlikesKey, memberId.toString());
            redisTemplate.opsForSet().remove(likesKey, memberId.toString());
        }

        return isLiked;
    }

    private Long getLikes(Post post) {
        String likesKey = StringUtils.join(POST_LIKES_PREFIX_KEY, post.getId());
        String unlikesKey = StringUtils.join(POST_UNLIKES_PREFIX_KEY, post.getId());
        Long likes = post.getLikes();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(likesKey))) {
            likes += redisTemplate.opsForSet().size(likesKey);
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey(unlikesKey))) {
            likes += redisTemplate.opsForSet().size(likesKey);
        }

        return likes;
    }

    private Long getViews(Post post) {
        Long postId = post.getId();
        Long views = post.getViews();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(StringUtils.join(POST_VIEWS_PREFIX_KEY, postId)))) {
            views += (Long) redisTemplate.opsForHash().get(POST_VIEWS_PREFIX_KEY, postId);
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
}
