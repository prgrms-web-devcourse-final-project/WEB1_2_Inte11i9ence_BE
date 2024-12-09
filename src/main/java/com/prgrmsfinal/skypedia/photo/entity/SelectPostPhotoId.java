package com.prgrmsfinal.skypedia.photo.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class SelectPostPhotoId implements Serializable {

	private Long postId;
	private Long photoId;

	// equals()와 hashCode()를 반드시 구현해야 함
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SelectPostPhotoId that = (SelectPostPhotoId)o;
		return Objects.equals(postId, that.postId) && Objects.equals(photoId, that.photoId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(postId, photoId);
	}
}