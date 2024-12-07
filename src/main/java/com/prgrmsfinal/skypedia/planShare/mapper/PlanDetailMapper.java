package com.prgrmsfinal.skypedia.planShare.mapper;

import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanDetail;

public class PlanDetailMapper {
	public static PlanDetailResponseDTO.Read toReadDto(PlanDetail planDetail) {
		return PlanDetailResponseDTO.Read.builder()
			.id(planDetail.getId())
			.nextPlanDetail(planDetail.getNextPlanDetail() != null ? toReadDto(planDetail.getNextPlanDetail()) : null)
			.prePlanDetail(planDetail.getPrePlanDetail() != null ? toReadDto(planDetail.getPrePlanDetail()) : null)
			.location(planDetail.getLocation())
			.placeId(planDetail.getPlaceId())
			.content(planDetail.getContent())
			.latitude(planDetail.getLatitude())
			.longitude(planDetail.getLongitude())
			.locationImage(planDetail.getLocationImage())
			.planDate(planDetail.getPlanDate())
			.build();
	}

	public static PlanDetailResponseDTO.Info toInfoDto(PlanDetail planDetail) {
		return PlanDetailResponseDTO.Info.builder()
			.id(planDetail.getId())
			.planGroupId(planDetail.getPlanGroup().getId())
			.location(planDetail.getLocation())
			.placeId(planDetail.getPlaceId())
			.content(planDetail.getContent())
			.latitude(planDetail.getLatitude())
			.longitude(planDetail.getLongitude())
			.locationImage(planDetail.getLocationImage())
			.planDate(planDetail.getPlanDate())
			.build();
	}

	public static PlanDetailResponseDTO.ReadAll toReadAllDto(PlanDetail planDetail) {
		return PlanDetailResponseDTO.ReadAll.builder()
			.id(planDetail.getId())
			.nextPlanDetail(planDetail.getNextPlanDetail() != null ? toReadDto(planDetail.getNextPlanDetail()) : null)
			.prePlanDetail(planDetail.getPrePlanDetail() != null ? toReadDto(planDetail.getPrePlanDetail()) : null)
			.location(planDetail.getLocation())
			.placeId(planDetail.getPlaceId())
			.content(planDetail.getContent())
			.latitude(planDetail.getLatitude())
			.longitude(planDetail.getLongitude())
			.locationImage(planDetail.getLocationImage())
			.planDate(planDetail.getPlanDate())
			.build();
	}
}

