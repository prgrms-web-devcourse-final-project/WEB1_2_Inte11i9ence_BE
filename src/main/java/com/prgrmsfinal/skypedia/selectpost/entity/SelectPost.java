package com.prgrmsfinal.skypedia.selectpost.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.photo.entity.SelectPostPhoto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "select_post")
public class SelectPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member; // 회원

	@Column(length = 1000)
	private String content; // 게시글 내용

	@Column(nullable = false)
	private boolean deleted = false; // 삭제 여부

	@Builder.Default
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Builder.Default
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();

	@Builder.Default
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt = null;

	@Builder.Default
	@OneToMany(mappedBy = "selectPost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SelectPostPhoto> photos = new ArrayList<>();

}