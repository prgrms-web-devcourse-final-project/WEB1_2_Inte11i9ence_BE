package com.prgrmsfinal.skypedia.photo.service;

import com.prgrmsfinal.skypedia.photo.dto.PhotoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotoService {
    String createPhotoURL(PhotoDTO photoDTO);
    List<String> createPhotoUrlList(List<PhotoDTO> photoDTOs);
    String readPhotoURL(Long photoId);
    List<String> readPhotoUrlList(List<Long> photoId);
    List<String> modifyPhotoUrlList(List<PhotoDTO> photoDTOs);
    void deletePhoto(String photoId);


    boolean checkContentType(PhotoDTO photoDTO);

}
