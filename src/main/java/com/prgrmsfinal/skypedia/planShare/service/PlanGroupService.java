package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;

import com.prgrmsfinal.skypedia.planShare.dto.PlanGroupDTO;

public interface PlanGroupService {
	List<PlanGroupDTO> readAll(PlanGroupDTO planGroupDTO);

	PlanGroupDTO read(Long id);

	PlanGroupDTO register(PlanGroupDTO planGroupDTO);

	PlanGroupDTO update(PlanGroupDTO planGroupDTO);

	void delete(Long id);
}
