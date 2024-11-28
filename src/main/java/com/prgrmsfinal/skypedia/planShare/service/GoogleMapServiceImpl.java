package com.prgrmsfinal.skypedia.planShare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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

            ResponseEntity<Map> response = restTemplate.getForEntity(geocodingUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                if (body != null && "OK".equals(body.get("status"))) {
                    Map<String, Object> location = extractLocation(body);

                    return Map.of(
                            "latitude", (Double) location.get("lat"),
                            "longitude", (Double) location.get("lng"));
                    }
                }
                throw new IllegalArgumentException("정보를 받아오는데 실패했습니다.");
            } catch (Exception e) {
            log.error("Error fetching coordinates: {}", e.getMessage());
            throw new RuntimeException("failed to fetch coordinates from Google API", e);
        }
        }

    // PlaceAPI - 장소 ID로 장소 이미지 가져옴
    @Override
    public String getPlacePhoto(String placeId) {
        try {
            String detailsUrl = String.format(
                    "https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&key=%s",
                    placeId, apiKey);

            ResponseEntity<Map> response = restTemplate.getForEntity(detailsUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                if (body != null && "OK".equals(body.get("status"))) {
                    return extraPhotoReference(body);
                }
            }
            throw new IllegalArgumentException("Failed to retrieve place photo.");
        } catch(Exception e){
            log.error("Error fetching place photo: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch place photo from Google API", e);
        }
    }

    private Map<String, Object> extractLocation(Map<String, Object> body) {
        List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");
        if (results != null && !results.isEmpty()) {
            Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
            return (Map<String, Object>) geometry.get("location");
        }
        throw new IllegalArgumentException("no results found for the given address.");
    }

    private String extraPhotoReference(Map<String, Object> body) {
        Map<String, Object> result = (Map<String, Object>) body.get("result");
        List<Map<String, Object>> photos = (List<Map<String, Object>>) result.get("photos");
        if (photos != null && !photos.isEmpty()) {
            return String.format(
                    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=%s&key=%s",
                    photos.get(0).get("photo_reference"), apiKey);
        }
        throw new IllegalArgumentException("no photos found for the given place ID");
    }
}
