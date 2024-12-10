package com.prgrmsfinal.skypedia.planShare.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleMapServiceImpl implements GoogleMapService {

	@Value("${google.api.key}") // 환경 설정에서 API 키 주입
	private String apiKey;

	private final RestTemplate restTemplate;

	// GeocodingAPI - 주소로 위도와 경도를 가져오는 기능
	@Override
	public Map<String, Double> getCoordinates(String address) {
		try {
			String geocodingUrl = String.format(
				"https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
				URLEncoder.encode(address, StandardCharsets.UTF_8), apiKey);

			Map<String, Object> response = fetchFromApi(geocodingUrl);

			Map<String, Object> location = extractLocation(response);

			return Map.of(
				"latitude", (Double)location.get("lat"),
				"longitude", (Double)location.get("lng"));
		} catch (Exception e) {
			log.error("Error fetching coordinates: {}", e.getMessage());
			throw new RuntimeException("Failed to fetch coordinates from Google API", e);
		}
	}

	// PlaceAPI - 장소 ID로 장소 이미지 가져옴
	@Override
	public String getPlacePhoto(String placeId) {
		String detailsUrl = String.format(
			"https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&key=%s",
			placeId, apiKey);

		Map<String, Object> response = fetchFromApi(detailsUrl);

		return extraPhotoReference(response);
	}

	// 공통 API 호출 및 응답 처리 메서드
	private Map<String, Object> fetchFromApi(String url) {
		ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			Map<String, Object> body = response.getBody();
			if (body != null && "OK".equals(body.get("status"))) {
				return body;
			}
		}
		throw new IllegalArgumentException("Failed to fetch valid response from Google API");
	}

	// 위치 정보 추출
	private Map<String, Object> extractLocation(Map<String, Object> body) {
		List<Map<String, Object>> results = (List<Map<String, Object>>)body.get("results");
		if (results != null && !results.isEmpty()) {
			Map<String, Object> geometry = (Map<String, Object>)results.get(0).get("geometry");
			return (Map<String, Object>)geometry.get("location");
		}
		throw new IllegalArgumentException("No results found for the given address.");
	}

	// 사진 참조 URL 추출
	private String extraPhotoReference(Map<String, Object> body) {
		Map<String, Object> result = (Map<String, Object>)body.get("result");
		List<Map<String, Object>> photos = (List<Map<String, Object>>)result.get("photos");
		if (photos != null && !photos.isEmpty()) {
			return String.format(
				"https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=%s&key=%s",
				photos.get(0).get("photo_reference"), apiKey);
		}
		throw new IllegalArgumentException("No photos found for the given place ID");
	}
}

