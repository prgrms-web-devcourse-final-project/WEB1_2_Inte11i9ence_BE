package com.prgrmsfinal.skypedia.planShare.mapper;

import java.util.List;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroup;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

public class PlanGroupMapper {
	public static PlanGroupResponseDTO.Read transDTO(PlanGroup planGroup, MemberResponseDTO.Info memberInfo,
		PlanGroupResponseDTO.Statistics statistics, List<PhotoResponseDTO.Info> photos,
		ReplyResponseDTO.ReadAll replies) {
		return PlanGroupResponseDTO.Read.builder()
			.id(planGroup.getId())
			.regionName(planGroup.getRegion().getRegionName())
			.title(planGroup.getTitle())
			.views(statistics.getViews())
			.likes(statistics.getLikes())
			.liked(statistics.isLiked())
			.scraped(statistics.isScraped())
			.updatedAt(planGroup.getUpdatedAt())
			.author(memberInfo)
			.photos(photos)
			.reply(replies)
			.build();
	}

	public static PlanGroupResponseDTO.Info transDTO(PlanGroupResponseDTO.Search planGroup,
		MemberResponseDTO.Info memberInfo
		, Long views, Long likes, Long replies, String thumbnailUrl) {
		return PlanGroupResponseDTO.Info.builder()
			.id(planGroup.getId())
			.author(memberInfo)
			.title(planGroup.getTitle())
			.views(views)
			.likes(likes)
			.replies(replies)
			.regionName(planGroup.getRegionName())
			.updatedAt(planGroup.getUpdatedAt())
			.thumbnailUrl(thumbnailUrl)
			.build();
	}

	public static PlanGroupResponseDTO.Info transDTO(PlanGroup planSearch, MemberResponseDTO.Info memberInfo
		, Long views, Long likes, Long replies, String thumbnailUrl) {
		return PlanGroupResponseDTO.Info.builder()
			.id(planSearch.getId())
			.author(memberInfo)
			.title(planSearch.getTitle())
			.views(views)
			.likes(likes)
			.replies(replies)
			.regionName(planSearch.getRegion().getRegionName())
			.updatedAt(planSearch.getUpdatedAt())
			.thumbnailUrl(thumbnailUrl)
			.build();
	}
}
