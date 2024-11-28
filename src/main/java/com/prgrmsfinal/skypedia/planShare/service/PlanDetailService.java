package com.prgrmsfinal.skypedia.planShare.service;

import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailDTO;

import java.util.List;

public interface PlanDetailService {
    List<PlanDetailDTO> readAll(PlanDetailDTO planDetailDTO);

    PlanDetailDTO read(Long id);

    PlanDetailDTO register(PlanDetailDTO planDetailDTO);

    PlanDetailDTO update(PlanDetailDTO planDetailDTO);

    void delete(Long id);
}
