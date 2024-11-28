package com.prgrmsfinal.skypedia.photo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDTO {
    private Long id;
    private String originalFileName;
    private String uuid; //서비스 생성
    private String contentType;
    private String s3FileKey; //서비스 생성
}
