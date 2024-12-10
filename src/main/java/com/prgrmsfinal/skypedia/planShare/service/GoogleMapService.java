package com.prgrmsfinal.skypedia.planShare.service;

import java.util.Map;

public interface GoogleMapService {
	Map<String, Double> getCoordinates(String address);

	String getPlacePhoto(String placeId);
}
