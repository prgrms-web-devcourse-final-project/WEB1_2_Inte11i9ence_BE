package com.prgrmsfinal.skypedia.planShare.mapper;

import java.util.List;

import com.prgrmsfinal.skypedia.member.dto.MemberResponseDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailResponseDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroup;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;

public class PlanGroupMapper {
	public static PlanGroupResponseDTO.Read toRead(PlanGroup planGroup, MemberResponseDTO.Info memberInfo,
		PlanGroupResponseDTO.Statistics statistics,
		List<PhotoResponseDTO.Info> photos,
		ReplyResponseDTO.ReadAll replies,
		List<PlanDetailResponseDTO.Read> planDetails) {
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
			.planDetails(planDetails) // PlanDetail의 content 리스트 추가
			.build();
	}

	public static PlanGroupResponseDTO.Info toInfo(PlanGroup planGroup) {
		return PlanGroupResponseDTO.Info.builder()
			.id(planGroup.getId())
			.title(planGroup.getTitle())
			.author(new MemberResponseDTO.Info(
				planGroup.getMember().getId(),
				planGroup.getMember().getUsername(),
				planGroup.getMember().getProfileImage()))
			.regionName(planGroup.getRegion() != null ? planGroup.getRegion().getRegionName() : null)
			.views(planGroup.getViews())
			.likes(planGroup.getLikes())
			.replies((long)planGroup.getPlanDetails().size())
			.updatedAt(planGroup.getUpdatedAt())
			.build();
	}
}
