package com.prgrmsfinal.skypedia.selectpost.service;

import com.prgrmsfinal.skypedia.photo.entity.Photo;
import com.prgrmsfinal.skypedia.selectpost.dto.SelectPostRequestDto;
import com.prgrmsfinal.skypedia.selectpost.dto.SelectPostResponseDto;

public interface SelectPostService {
	SelectPostResponseDto.ListResponse readAllSelectPosts(int size);
	
	SelectPostResponseDto createSelectPost(Long memberId, SelectPostRequestDto requestDto);

	String determinePhotoCategory(Photo photo);

	SelectPostResponseDto readSelectPost(Long selectPostId);

	SelectPostResponseDto updateSelectPost(Long selectPostId, Long memberId, SelectPostRequestDto requestDto);

	void deleteSelectPost(Long selectPostId, Long memberId);
}
