package com.prgrmsfinal.skypedia.planShare.service;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;

public interface GoogleMapService {
	// Map<String, Double> getCoordinates(String address);
	String fetchPlaceImage(String location);

	Map<String, Object> fetchCoordinates(@NotBlank(message = "세부 일정의 장소명 입력은 필수입니다.") String location);

	// String getPlacePhoto(String placeId);

	// String fetchPlacePhotoUrl(String placeId);
}
