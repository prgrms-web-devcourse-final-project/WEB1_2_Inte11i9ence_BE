package com.prgrmsfinal.skypedia.planShare.service;

import com.prgrmsfinal.skypedia.planShare.dto.RegionDTO;

import java.util.List;

public interface RegionService {
    List<RegionDTO> readAll(RegionDTO regionDTO);

    RegionDTO read(Long id);

    RegionDTO register(RegionDTO regionDTO);

    RegionDTO update(RegionDTO regionDTO);

    void delete(Long id);
}
