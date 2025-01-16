package com.prgrmsfinal.skypedia.planShare.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.prgrmsfinal.skypedia.global.exception.CommonException;
import com.prgrmsfinal.skypedia.planShare.exception.PlanError;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleMapServiceImpl implements GoogleMapService {

	@Value("${google.api.key}")
	private String apiKey;

	private final WebClient webClient;

	public String fetchPlaceImage(String location) {
		if (location == null || location.trim().isEmpty()) {
			throw new IllegalArgumentException("Input address or place name must not be empty.");
		}

		Map<String, Object> locationData = fetchCoordinates(location);
		String placeId = (String)locationData.get("placeId");

		return fetchPlacePhotoUrl(placeId);
	}

	public Map<String, Object> fetchCoordinates(String address) {
		String geocodingUrl = "/maps/api/geocode/json";

		Map<String, Object> response = fetchFromApi(geocodingUrl, Map.of("address", address, "key", apiKey));

		List<Map<String, Object>> results = (List<Map<String, Object>>)response.get("results");
		if (results == null || results.isEmpty()) {
			throw new RuntimeException("No results found for the address: " + address);
		}

		Map<String, Object> geometry = (Map<String, Object>)results.get(0).get("geometry");
		Map<String, Object> location = (Map<String, Object>)geometry.get("location");

		String placeId = (String)results.get(0).get("place_id");

		return Map.of(
			"latitude", location.get("lat"),
			"longitude", location.get("lng"),
			"placeId", placeId
		);
	}

	private String fetchPlacePhotoUrl(String placeId) {
		String detailsUrl = "/maps/api/place/details/json";

		Map<String, Object> response = fetchFromApi(detailsUrl, Map.of("place_id", placeId, "key", apiKey));

		Map<String, Object> result = (Map<String, Object>)response.get("result");
		List<Map<String, Object>> photos = (List<Map<String, Object>>)result.get("photos");
		if (photos == null || photos.isEmpty()) {
			throw new RuntimeException("No photos found for the place ID: " + placeId);
		}

		String photoReference = (String)photos.get(0).get("photo_reference");

		return String.format(
			"https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=%s&key=%s",
			photoReference, apiKey
		);
	}

	private Map<String, Object> fetchFromApi(String endpoint, Map<String, String> queryParams) {
		return webClient.get()
			.uri(uriBuilder -> {
				uriBuilder.path(endpoint);
				queryParams.forEach(uriBuilder::queryParam);
				return uriBuilder.build();
			})
			.retrieve()
			.onStatus(status -> status.is4xxClientError(),
				response -> handleError(response, PlanError.BAD_REQUEST)) // HttpStatus 비교
			.onStatus(status -> status.is5xxServerError(),
				response -> handleError(response, PlanError.NOT_FETCHED)) // HttpStatus 비교
			.bodyToMono(Map.class)
			.block();
	}

	private Mono<Throwable> handleError(ClientResponse response, PlanError planError) {
		return response.bodyToMono(String.class)
			.map(message -> {
				log.error("Error occurred: {} / Detail: {}", planError.getException().getMessage(), message);

				return new CommonException(
					planError.getException().getCode(),
					planError.getException().getMessage() + " / Detail: " + message
				);
			});
	}
}

