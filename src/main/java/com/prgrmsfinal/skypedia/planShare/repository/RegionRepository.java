package com.prgrmsfinal.skypedia.planShare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.planShare.dto.RegionResponseDTO;
import com.prgrmsfinal.skypedia.planShare.entity.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
	@Query("SELECT r FROM Region r WHERE r.regionName = :regionName")
	RegionResponseDTO findByRegionName(@Param("regionName") String regionName);

	@Query(value = "SELECT EXISTS (SELECT 1 FROM region WHERE region.Name = :regionName)", nativeQuery = true)
	boolean existsByRegionName(@Param("regionName") String regionName);
}
