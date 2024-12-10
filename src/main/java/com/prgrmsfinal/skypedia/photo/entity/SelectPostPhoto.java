package com.prgrmsfinal.skypedia.photo.entity;

import com.prgrmsfinal.skypedia.selectpost.entity.SelectPost;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "select_post_photo")
public class SelectPostPhoto {

	@EmbeddedId
	private SelectPostPhotoId id = new SelectPostPhotoId();

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("postId")
	@JoinColumn(name = "post_id")
	private SelectPost selectPost; // 사진 선택 게시글

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("photoId")
	@JoinColumn(name = "photo_id")
	private Photo photo; // 사진

	@Column(nullable = false, length = 2)
	private String category; // 카테고리 ('인물', '배경')

	@Column(nullable = false)
	private Long likes = 0L; // 좋아요 수

}