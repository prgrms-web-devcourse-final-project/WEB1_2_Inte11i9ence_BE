package com.prgrmsfinal.skypedia.planShare.service;

import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupDTO;
import com.prgrmsfinal.skypedia.planShare.entity.PlanGroup;
import com.prgrmsfinal.skypedia.planShare.exception.PlanError;
import com.prgrmsfinal.skypedia.planShare.repository.PlanGroupRepository;
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
public class PlanGroupServiceImpl implements PlanGroupService {
    private final PlanGroupRepository planGroupRepository;

    @Override
    public List<PlanGroupDTO> readAll(PlanGroupDTO planGroupDTO) {
        List<PlanGroup> planGroups = planGroupRepository.findAll();
        return planGroups.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public PlanGroupDTO read(Long id) {
        Optional<PlanGroup> planGroup = planGroupRepository.findById(id);
        return planGroup.map(this::entityToDto).orElseThrow(PlanError.NOT_FOUND::get);
    }

    @Override
    @Transactional
    public PlanGroupDTO register(PlanGroupDTO planGroupDTO) {
        try {
            PlanGroup planGroup = dtoToEntity(planGroupDTO);
            planGroup = planGroupRepository.save(planGroup);
            return entityToDto(planGroup);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw PlanError.NOT_REGISTERED.get();
        }
    }

    @Override
    public PlanGroupDTO update(PlanGroupDTO planGroupDTO) {
        Optional<PlanGroup> updatePlan = planGroupRepository.findById(planGroupDTO.getId());
        PlanGroup planGroup = updatePlan.orElseThrow(PlanError.NOT_FOUND::get);

        try {
            planGroup.setGroupImage(planGroupDTO.getGroupImage());
            planGroup.setTitle(planGroupDTO.getTitle());

            planGroupRepository.save(planGroup);

            return entityToDto(planGroup);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw PlanError.NOT_MODIFIED.get();
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PlanGroup planGroup = planGroupRepository.findById(id).orElseThrow(PlanError.NOT_FOUND::get);
        planGroupRepository.delete(planGroup);
    }

    private PlanGroupDTO entityToDto(PlanGroup planGroup) {
        return PlanGroupDTO.builder()
                .id(planGroup.getId())
                .memberId(planGroup.getMember().getId())
                .regionId(planGroup.getRegion().getId())
                .title(planGroup.getTitle())
                .groupImage(planGroup.getGroupImage())
                .likes(planGroup.getLikes())
                .deleted(planGroup.getDeleted())
                .createdAt(planGroup.getCreatedAt())
                .updatedAt(planGroup.getUpdatedAt())
                .deletedAt(planGroup.getDeletedAt())
                .build();
    }

    private PlanGroup dtoToEntity(PlanGroupDTO planGroupDTO) {
        return PlanGroup.builder()
                .id(planGroupDTO.getId())
                .title(planGroupDTO.getTitle())
                .groupImage(planGroupDTO.getGroupImage())
                .likes(planGroupDTO.getLikes())
                .deleted(planGroupDTO.getDeleted())
                .createdAt(planGroupDTO.getCreatedAt())
                .updatedAt(planGroupDTO.getUpdatedAt())
                .deletedAt(planGroupDTO.getDeletedAt())
                .build();
    }
}
