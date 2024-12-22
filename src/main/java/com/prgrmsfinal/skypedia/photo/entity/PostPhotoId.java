package com.prgrmsfinal.skypedia.photo.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostPhotoId implements Serializable {
	private Long postId;
	private Long photoId;
}