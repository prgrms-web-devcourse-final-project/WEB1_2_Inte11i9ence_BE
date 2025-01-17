package com.prgrmsfinal.skypedia.post.util;

import java.util.Arrays;
import java.util.List;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

import io.micrometer.common.util.StringUtils;

public class PostMapper {
	public static PostResponseDTO.Read toDTO(Post post, MemberResponseDTO.Info memberInfo,
		PostResponseDTO.Statistics postStatistics, List<PhotoResponseDTO.Info> photos,
		ReplyResponseDTO.ReadAll replies) {
		return PostResponseDTO.Read.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.rating(post.getRating())
			.postedAt(post.getCreatedAt())
			.category(post.getCategory().getName())
			.author(memberInfo)
			.views(postStatistics.getViews())
			.likes(postStatistics.getLikes())
			.liked(postStatistics.isLiked())
			.scraped(postStatistics.isScraped())
			.photos(photos)
			.reply(replies)
			.hashtags(Arrays.stream(post.getHashtags().split(","))
				.filter(StringUtils::isNotBlank).toList())
			.build();
	}

	public static PostResponseDTO.Info toDTO(Post post, MemberResponseDTO.Info memberInfo
		, Long views, Long likes, Long replies, String photoUrl) {
		return PostResponseDTO.Info.builder()
			.id(post.getId())
			.author(memberInfo)
			.title(post.getTitle())
			.content(post.getContent())
			.views(views)
			.likes(likes)
			.replies(replies)
			.category(post.getCategory().getName())
			.rating(post.getRating())
			.postedAt(post.getCreatedAt())
			.photoUrl(photoUrl)
			.build();
	}
}
