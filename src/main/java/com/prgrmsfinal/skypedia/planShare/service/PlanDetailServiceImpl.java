package com.prgrmsfinal.skypedia.planShare.service;

import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanDetail;
import com.prgrmsfinal.skypedia.planShare.exception.PlanError;
import com.prgrmsfinal.skypedia.planShare.repository.PlanDetailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanDetailServiceImpl implements PlanDetailService {
    private final PlanDetailRepository planDetailRepository;

    @Override
    public List<PlanDetailDTO> readAll(PlanDetailDTO planDetailDTO) {
        List<PlanDetail> planDetails = planDetailRepository.findAll();
        return planDetails.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public PlanDetailDTO read(Long id) {
        Optional<PlanDetail> planDetail = planDetailRepository.findById(id);
        return planDetail.map(this::entityToDto).orElseThrow(PlanError.NOT_FOUND::get);
    }

    @Override
    @Transactional
    public PlanDetailDTO register(PlanDetailDTO planDetailDTO) {
        try {
            PlanDetail planDetail = dtoToEntity(planDetailDTO);
            planDetail = planDetailRepository.save(planDetail);
            return entityToDto(planDetail);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw PlanError.NOT_REGISTERED.get();
        }
    }

    @Override
    public PlanDetailDTO update(PlanDetailDTO planDetailDTO) {
        Optional<PlanDetail> updatePlanDetail = planDetailRepository.findById(planDetailDTO.getId());
        PlanDetail planDetail1 = updatePlanDetail.orElseThrow(PlanError.NOT_FOUND::get);

        try {
            planDetail1.setContent(planDetailDTO.getContent());
            planDetail1.setPlanDate(planDetailDTO.getPlanDate());
            planDetail1.setLatitude(planDetailDTO.getLatitude());
            planDetail1.setLongitude(planDetailDTO.getLongitude());
            planDetail1.setLocation(planDetailDTO.getLocation());
            planDetail1.setLocationImage(planDetailDTO.getLocationImage());

            planDetailRepository.save(planDetail1);

            return entityToDto(planDetail1);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw PlanError.NOT_MODIFIED.get();
        }
    }

    @Override
    public void delete(Long id) {
        PlanDetail planDetail = planDetailRepository.findById(id).orElseThrow(PlanError.NOT_FOUND::get);
        planDetailRepository.delete(planDetail);
    }

    private PlanDetailDTO entityToDto(PlanDetail planDetail) {
        return PlanDetailDTO.builder()
                .id(planDetail.getId())
                .planGroupId(planDetail.getPlanGroup().getId())
                .location(planDetail.getLocation())
                .content(planDetail.getContent())
                .latitude(planDetail.getLatitude())
                .longitude(planDetail.getLongitude())
                .locationImage(planDetail.getLocationImage())
                .planDate(planDetail.getPlanDate())
                .deleted(planDetail.getDeleted())
                .createdAt(planDetail.getCreatedAt())
                .updatedAt(planDetail.getUpdatedAt())
                .deletedAt(planDetail.getDeletedAt())
                .build();
    }

    private PlanDetail dtoToEntity(PlanDetailDTO planDetailDTO) {
        return PlanDetail.builder()
                .id(planDetailDTO.getId())
                .location(planDetailDTO.getLocation())
                .content(planDetailDTO.getContent())
                .latitude(planDetailDTO.getLatitude())
                .longitude(planDetailDTO.getLongitude())
                .locationImage(planDetailDTO.getLocationImage())
                .planDate(planDetailDTO.getPlanDate())
                .deleted(planDetailDTO.getDeleted())
                .createdAt(planDetailDTO.getCreatedAt())
                .updatedAt(planDetailDTO.getUpdatedAt())
                .deletedAt(planDetailDTO.getDeletedAt())
                .build();
    }
}
