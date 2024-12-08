package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.post.entity.Post;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "post_photo")
public class PostPhoto {

	@EmbeddedId
	private PostPhotoId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("postId")
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("photoId")
	@JoinColumn(name = "photo_id", nullable = false)
	private Photo photo;

	// Getters, Setters, Constructors
}