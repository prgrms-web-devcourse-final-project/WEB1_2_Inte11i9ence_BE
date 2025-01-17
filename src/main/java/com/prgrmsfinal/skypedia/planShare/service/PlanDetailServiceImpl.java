package com.prgrmsfinal.skypedia.planShare.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanDetail;
import com.prgrmsfinal.skypedia.planShare.mapper.PlanDetailMapper;
import com.prgrmsfinal.skypedia.planShare.repository.PlanDetailRepository;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlanDetailServiceImpl implements PlanDetailService {

	private final PlanDetailRepository planDetailRepository;
	private final PlanGroupRepository planGroupRepository;
	private final GoogleMapService googleMapService;

	@Override
	public List<PlanDetailResponseDTO.ReadAll> readAll(PlanDetailResponseDTO.ReadAll detailReadAll) {
		List<PlanDetail> planDetails = planDetailRepository.findAllByPlanGroupId(detailReadAll.getId());

		planDetails.sort(Comparator.comparing(PlanDetail::getPlanDate).thenComparing(PlanDetail::getUpdatedAt));

		return planDetails.stream()
			.filter(planDetail -> Boolean.FALSE.equals(planDetail.getDeleted())) // 논리 삭제된 데이터를 제외
			.map(planDetail -> {
				PlanDetailResponseDTO.ReadAll dto = PlanDetailMapper.toReadAllDto(planDetail);
				String location = planDetail.getLocation();
				String placeImageUrl = "default-image-url"; // 기본 URL 설정

				try {
					if (location != null && !location.isBlank()) {
						placeImageUrl = googleMapService.fetchPlaceImage(location);
					}
				} catch (Exception e) {
					log.warn("Failed to fetch place image for '{}': {}", location, e.getMessage());
				}

				return dto;
			})
			.toList();
	}

	@Override
	public PlanDetailRequestDTO.Create register(PlanDetailRequestDTO.Create planDetailDTO) {
		Map<String, Object> coordinatesMap = Map.of("latitude", 0.0, "longitude", 0.0);
		String placePhotoUrl = "default-image-url";

		try {
			coordinatesMap = googleMapService.fetchCoordinates(planDetailDTO.getLocation());
			placePhotoUrl = googleMapService.fetchPlaceImage(planDetailDTO.getLocation());
		} catch (Exception e) {
			log.warn("[GoogleMap] Failed to fetch coordinates or photo for '{}': {}", planDetailDTO.getLocation(),
				e.getMessage());
		}

		// PlanGroup planGroup = planGroupRepository.findById(planDetailDTO.setNextPlanDetail())
		// 	.orElseThrow(() -> new IllegalArgumentException("PlanGroup을 찾을 수 없습니다."));

		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		Point coordinates = geometryFactory.createPoint(new Coordinate(
			(Double)coordinatesMap.get("longitude"),
			(Double)coordinatesMap.get("latitude")
		));

		PlanDetail planDetail = PlanDetail.builder()
			.location(planDetailDTO.getLocation())
			.placeId(planDetailDTO.getPlaceId())
			.content(planDetailDTO.getContent())
			.locationImage(placePhotoUrl)
			.planDate(planDetailDTO.getPlanDate())
			.coordinates(coordinates)
			.deleted(false)
			// .planGroup(planGroup)
			.build();

		// PlanDetail lastDetail = planGroup.getPlanDetails().stream()
		// 	.reduce((first, second) -> second)
		// 	.orElse(null);
		// if (lastDetail != null) {
		// 	lastDetail.linkNext(planDetail);
		// }

		// planGroup.getPlanDetails().add(planDetail);
		planDetailRepository.save(planDetail);

		return planDetailDTO;
	}

	// Update PlanDetail
	@Override
	public PlanDetailRequestDTO.Update update(Long id, PlanDetailRequestDTO.Update planDetailDTO) {
		PlanDetail planDetail = planDetailRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("PlanDetail을 찾을 수 없습니다."));

		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		Point coordinates = geometryFactory.createPoint(new Coordinate(
			planDetailDTO.getLongitude(),
			planDetailDTO.getLatitude()
		));

		planDetail.updateDetails(
			planDetailDTO.getContent(),
			planDetailDTO.getPlanDate(),
			coordinates,
			planDetailDTO.getLocation(),
			planDetailDTO.getLocationImage()
		);

		planDetailRepository.save(planDetail);
		return PlanDetailMapper.toUpdateDto(planDetail);
	}

	@Override
	public void delete(Long id) {
		PlanDetail planDetail = planDetailRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("PlanDetail을 찾을 수 없습니다."));

		planDetail.removePlanDetail(); // 논리 삭제 및 연결 처리
		planDetailRepository.save(planDetail);
	}

	@Override
	public void reorder(Long targetId, Long newPrevId, Long newNextId) {
		PlanDetail target = planDetailRepository.findById(targetId)
			.orElseThrow(() -> new IllegalArgumentException("대상 PlanDetail을 찾을 수 없습니다."));
		PlanDetail newPrev = newPrevId != null ? planDetailRepository.findById(newPrevId).orElse(null) : null;
		PlanDetail newNext = newNextId != null ? planDetailRepository.findById(newNextId).orElse(null) : null;

		target.removePlanDetail();

		if (newPrev != null) {
			newPrev.linkNext(target);
		} else if (newNext != null) {
			newNext.getPrePlanDetail().linkNext(target);
		}

		planDetailRepository.save(target);
	}
}
