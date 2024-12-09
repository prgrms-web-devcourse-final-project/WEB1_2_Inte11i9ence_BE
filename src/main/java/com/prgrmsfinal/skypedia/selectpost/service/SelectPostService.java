package com.prgrmsfinal.skypedia.selectpost.service;

import java.util.List;

import com.prgrmsfinal.skypedia.photo.dto.PhotoResponseDTO;
import com.prgrmsfinal.skypedia.photo.entity.Photo;
import com.prgrmsfinal.skypedia.selectpost.dto.SelectPostRequestDto;
import com.prgrmsfinal.skypedia.selectpost.dto.SelectPostResponseDto;

public interface SelectPostService {
	SelectPostResponseDto.ListResponse readAllSelectPosts(int size);

	List<PhotoResponseDTO.Info> createSelectPost(Long memberId, SelectPostRequestDto requestDto);

	String determinePhotoCategory(Photo photo);

	SelectPostResponseDto readSelectPost(Long selectPostId);

	SelectPostResponseDto updateSelectPost(Long selectPostId, Long memberId, SelectPostRequestDto requestDto);

	void deleteSelectPost(Long selectPostId, Long memberId);
}
