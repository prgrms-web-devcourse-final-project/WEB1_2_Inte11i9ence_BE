package com.prgrmsfinal.skypedia.selectpost.dto;

import java.util.List;

import com.prgrmsfinal.skypedia.photo.dto.PhotoRequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectPostRequestDto {
	private String content;
	private List<PhotoRequestDTO.Upload> uploads;  // PhotoService의 uploadPhotosForPost와 맞추기
}