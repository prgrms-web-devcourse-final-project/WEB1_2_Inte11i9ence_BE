package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;

import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailDTO;

public interface PlanDetailService {
	List<PlanDetailDTO> readAll(PlanDetailDTO planDetailDTO);

	PlanDetailDTO read(Long id);

	PlanDetailDTO register(PlanDetailDTO planDetailDTO);

	PlanDetailDTO update(PlanDetailDTO planDetailDTO);

	void delete(Long id);
}
