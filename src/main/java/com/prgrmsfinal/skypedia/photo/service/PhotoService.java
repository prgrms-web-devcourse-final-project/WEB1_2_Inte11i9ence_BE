package com.prgrmsfinal.skypedia.photo.service;

import java.util.List;

import com.prgrmsfinal.skypedia.photo.dto.PhotoDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoRequestDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;

public interface PhotoService {
	String createPhotoURL(PhotoDTO photoDTO);

	List<String> createPhotoUrlList(List<PhotoDTO> photoDTOs);

	String readPhotoURL(Long photoId);

	List<String> readPhotoUrlList(List<Long> photoId);

	List<String> modifyPhotoUrlList(List<PhotoDTO> photoDTOs);
	
	boolean checkContentType(PhotoDTO photoDTO);

	List<PhotoResponseDTO.Info> readPhotosByPostId(Long postId);

	List<PhotoResponseDTO.Info> uploadPhotosForPost(Long postId, List<PhotoRequestDTO.Upload> uploads);

	PhotoResponseDTO.Info uploadSinglePhotoForPost(Long postId, PhotoRequestDTO.Upload upload);
}

