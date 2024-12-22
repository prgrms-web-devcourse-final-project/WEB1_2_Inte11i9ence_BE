package com.prgrmsfinal.skypedia.planShare.service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanDetail;
import com.prgrmsfinal.skypedia.planShare.exception.PlanError;
import com.prgrmsfinal.skypedia.planShare.repository.PlanDetailRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanDetailServiceImpl implements PlanDetailService {
	private final PlanDetailRepository planDetailRepository;
	private final GoogleMapService googleMapService;

	private final LinkedList<PlanDetail> planDetails = new LinkedList<>();
	private Long idCounter = 1L;

	@Override
	public List<PlanDetailResponseDTO.ReadAll> readAll(PlanDetailResponseDTO.ReadAll DetailReadAll) {
		LinkedList<PlanDetail> sortedPlanDetails = planDetails.stream()
			.sorted(Comparator.comparing(PlanDetail::getPlanDate)
				.thenComparing(PlanDetail::getUpdatedAt))
			.collect(Collectors.toCollection(LinkedList::new));

		return sortedPlanDetails.stream()
			.map(planDetail -> PlanDetailResponseDTO.ReadAll.builder()
				.id(planDetail.getId())
				.location(planDetail.getLocation())
				.content(planDetail.getContent())
				.planDate(planDetail.getPlanDate())
				.updatedAt(planDetail.getUpdatedAt())
				.build())
			.toList();
	}

	@Override
	@Transactional
	public PlanDetailRequestDTO.Create register(PlanDetailRequestDTO.Create planDetailDTO) {
		Map<String, Double> coordinates = googleMapService.getCoordinates(planDetailDTO.getLocation());
		String placePhotoUrl = googleMapService.getPlacePhoto(planDetailDTO.getPlaceId());

		PlanDetail planDetail = PlanDetail.builder()
			.location(planDetailDTO.getLocation())
			.placeId(planDetailDTO.getPlaceId())
			.content(planDetailDTO.getContent())
			.locationImage(placePhotoUrl)
			.planDate(planDetailDTO.getPlanDate())
			.build();

		planDetail.updateCoordinates(coordinates.get("latitude"), coordinates.get("longitude"));

		planDetails.add(planDetail);

		return planDetailDTO;
	}

	@Override
	public PlanDetailRequestDTO.Update update(Long id, PlanDetailRequestDTO.Update planDetailDTO) {
		PlanDetail planDetail = planDetails.stream()
			.filter(detail -> detail.getId().equals(planDetailDTO.getId()))
			.findFirst()
			.orElseThrow(PlanError.NOT_FOUND::getException);

		planDetail.updateDetails(
			planDetailDTO.getContent(),
			planDetailDTO.getPlanDate(),
			planDetailDTO.getLatitude(),
			planDetailDTO.getLongitude(),
			planDetailDTO.getLocation(),
			planDetailDTO.getLocationImage()
		);

		return PlanDetailRequestDTO.Update.builder()
			.location(planDetail.getLocation())
			.regionName(planDetailDTO.getRegionName())
			.content(planDetail.getContent())
			.latitude(planDetail.getLatitude())
			.longitude(planDetail.getLongitude())
			.locationImage(planDetail.getLocationImage())
			.planDate(planDetail.getPlanDate())
			.build();
	}

	@Override
	public void delete(Long id) {
		boolean remove = planDetails.removeIf(detail -> detail.getId().equals(id));
		if (!remove) {
			throw PlanError.NOT_FOUND.getException();
		}
	}
}
