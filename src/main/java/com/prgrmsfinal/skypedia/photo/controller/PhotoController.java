package com.prgrmsfinal.skypedia.photo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgrmsfinal.skypedia.photo.dto.PhotoDTO;
import com.prgrmsfinal.skypedia.photo.service.PhotoService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/photo")
public class PhotoController {
	private final PhotoService photoService;

	// ------------- GETMAPPING --------------------

	// ?ids=1,2,3

	@GetMapping
	public Map<String, List<String>> readPhotoList(@RequestParam List<Long> ids) {
		List<String> photoUrls = photoService.readPhotoUrlList(ids);
		return Map.of("photos", photoUrls);
	}

	// --------------- POSTMAPPING ---------------------

    /*
        contentType = image/jpeg
        originalFileName = django.jpg
     */

	@PostMapping
	public Map<String, List<String>> createPhotoList(@RequestBody List<PhotoDTO> photoDTOs) {
		List<String> photoUrls = photoService.createPhotoUrlList(photoDTOs);
		return Map.of("photos", photoUrls);
	}

	// ---------------- PUTMAPPING ------------------------

    /*
        id = 1
        contentType = image/jpeg
        originalFileName = django.jpg
     */

	@PutMapping
	public List<String> updatePhoto(@RequestBody List<PhotoDTO> photoDTOs) {
		System.out.println();
		return photoService.modifyPhotoUrlList(photoDTOs);
	}
}
