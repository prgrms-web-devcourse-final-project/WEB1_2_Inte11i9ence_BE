package com.prgrmsfinal.skypedia.planShare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class GoogleMapServiceImplTest {

	@Mock
	private RestTemplate restTemplate; // RestTemplate을 Mock으로 생성

	@InjectMocks
	private GoogleMapServiceImpl googleMapService; // 테스트할 클래스 주입

	@Value("${google.api.key}")
	private String apiKey; // 환경 변수에서 API 키 주입

	@Test
	void testGetCoordinates_success() {
		// given
		String address = "Seoul";
		String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
		String url = String.format(
			"https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
			encodedAddress, apiKey);

		// Mock된 API 응답
		Map<String, Object> mockResponse = Map.of(
			"status", "OK",
			"results", List.of(
				Map.of(
					"geometry", Map.of(
						"location", Map.of(
							"lat", 37.5665,
							"lng", 126.9780
						)
					)
				)
			)
		);

		when(restTemplate.getForEntity(url, Map.class))
			.thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

		// when
		Map<String, Double> coordinates = googleMapService.getCoordinates(address);

		// then
		assertNotNull(coordinates);
		assertEquals(37.5665, coordinates.get("latitude"));
		assertEquals(126.9780, coordinates.get("longitude"));
	}

	@Test
	void testGetPlacePhoto_success() {
		// given
		String placeId = "ChIJd_Y0eVIvkFQR0iyWr8zHAAQ";
		String url = String.format(
			"https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&key=%s",
			placeId, apiKey);

		// Mock된 API 응답
		Map<String, Object> mockResponse = Map.of(
			"status", "OK",
			"result", Map.of(
				"photos", List.of(
					Map.of("photo_reference", "photoRef12345")
				)
			)
		);

		when(restTemplate.getForEntity(url, Map.class))
			.thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

		// when
		String photoUrl = googleMapService.getPlacePhoto(placeId);

		// then
		assertNotNull(photoUrl);
		assertTrue(photoUrl.contains("photoRef12345"));
	}

	@Test
	void testGetCoordinates_failure() {
		// given
		String address = "UnknownPlace";
		String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
		String url = String.format(
			"https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
			encodedAddress, apiKey);

		// Mock된 실패 응답
		Map<String, Object> mockResponse = Map.of("status", "ZERO_RESULTS");

		when(restTemplate.getForEntity(url, Map.class))
			.thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

		// when & then
		assertThrows(RuntimeException.class, () -> googleMapService.getCoordinates(address));
	}

	@Test
	void testGetPlacePhoto_failure() {
		// given
		String placeId = "InvalidPlaceId";
		String url = String.format(
			"https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&key=%s",
			placeId, apiKey);

		// Mock된 실패 응답
		Map<String, Object> mockResponse = Map.of("status", "INVALID_REQUEST");

		when(restTemplate.getForEntity(url, Map.class))
			.thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

		// when & then
		assertThrows(RuntimeException.class, () -> googleMapService.getPlacePhoto(placeId));
	}
}
