package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;

import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailRequestDTO;
import com.prgrmsfinal.skypedia.planShare.dto.PlanDetailResponseDTO;

public interface PlanDetailService {
	List<PlanDetailResponseDTO.ReadAll> readAll(PlanDetailResponseDTO.ReadAll planDetailDTO);

	PlanDetailRequestDTO.Create register(PlanDetailRequestDTO.Create detailCreate);

	PlanDetailRequestDTO.Update update(Long id, PlanDetailRequestDTO.Update detailUpdate);

	void delete(Long id);
}
