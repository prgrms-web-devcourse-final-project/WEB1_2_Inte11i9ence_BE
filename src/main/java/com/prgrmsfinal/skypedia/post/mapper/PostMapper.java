package com.prgrmsfinal.skypedia.post.mapper;

import java.util.List;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.post.dto.PostResponseDTO;
import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

public class PostMapper {
	public static PostResponseDTO.Read getDTO(Post post, MemberResponseDTO.Info memberInfo,
		PostResponseDTO.Statistics postStatistics, List<PhotoResponseDTO.Info> photos,
		List<ReplyResponseDTO.ReadAll> replies, String nextReplyUrl) {
		return PostResponseDTO.Read.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.rating(post.getRating())
			.postedAt(post.getUpdatedAt())
			.category(post.getCategory().getName())
			.author(memberInfo)
			.views(postStatistics.getViews())
			.likes(postStatistics.getLikes())
			.liked(postStatistics.isLiked())
			.scraped(postStatistics.isScraped())
			.photos(photos)
			.replies(replies)
			.nextReplyUrl(nextReplyUrl)
			.build();
	}

	public static PostResponseDTO.Info getDTO(Post post, MemberResponseDTO.Info memberInfo
		, PostResponseDTO.Statistics postStatistics, Long replies, String photoUrl) {
		return PostResponseDTO.Info.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.views(postStatistics.getViews())
			.likes(postStatistics.getLikes())
			.replies(replies)
			.category(post.getCategory().getName())
			.rating(post.getRating())
			.postedAt(post.getUpdatedAt())
			.photoUrl(photoUrl)
			.build();
	}

	public static PostResponseDTO.ReadAll getDTO(List<PostResponseDTO.Info> posts, String nextPostUrl) {
		return new PostResponseDTO.ReadAll(posts, nextPostUrl);
	}
}
