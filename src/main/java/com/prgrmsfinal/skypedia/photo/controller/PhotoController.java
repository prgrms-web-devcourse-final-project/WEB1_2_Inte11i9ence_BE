package com.prgrmsfinal.skypedia.photo.controller;


import com.prgrmsfinal.skypedia.photo.dto.PhotoDTO;
import com.prgrmsfinal.skypedia.photo.repository.PhotoRepository;
import com.prgrmsfinal.skypedia.photo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/photo")
public class PhotoController {
    private final PhotoService photoService;

    // ------------- GETMAPPING --------------------

    // ?ids=1,2,3

    @GetMapping
    public List<String> readPhotoList(@RequestParam List<Long> ids) {
        return photoService.readPhotoUrlList(ids);
    }

    // --------------- POSTMAPPING ---------------------

    /*
        contentType = image/jpeg
        originalFileName = django.jpg
     */

    @PostMapping
    public List<String> createPhotoList(@RequestBody List<PhotoDTO> photoDTOs) {
        return photoService.createPhotoUrlList(photoDTOs);
    }


    // ---------------- PUTMAPPING ------------------------

    /*
        id = 1
        contentType = image/jpeg
        originalFileName = django.jpg
     */

    @PutMapping
    public List<String> updatePhoto(@RequestBody List<PhotoDTO> photoDTOs) {
        return photoService.modifyPhotoUrlList(photoDTOs);
    }
}
