package com.prgrmsfinal.skypedia.photo.service;

import java.util.List;

import com.prgrmsfinal.skypedia.photo.dto.PhotoDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoRequestDTO;
import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;

public interface PhotoService {

	//리퀘스트dto로 업로드된 값들을 가져와 id, url을 List로 반환
	List<PhotoResponseDTO.Info> createPhotoUrlList(List<PhotoRequestDTO.Upload> photoDTOs);

	List<String> readPhotoUrlList(List<Long> photoId);

	List<String> modifyPhotoUrlList(List<PhotoDTO> photoDTOs);

	String readPhotoUrlByPostId(Long postId);

	List<PhotoResponseDTO.Info> readPhotoUrlListByPostId(Long postId);

	boolean checkContentType(PhotoRequestDTO.Upload photoDTO);

	List<PhotoResponseDTO.Info> uploadPhotosForPost(Long postId, List<PhotoRequestDTO.Upload> uploads);

	PhotoResponseDTO.Info uploadSinglePhotoForPost(Long postId, PhotoRequestDTO.Upload upload);
}

