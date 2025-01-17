package com.prgrmsfinal.skypedia.member.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class})
@FilterDef(name = "withdrawnFilter", parameters = @ParamDef(name = "withdrawn", type = Boolean.class))
@Filter(name = "withdrawnFilter", condition = "withdrawn = :withdrawn")
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;                //자동생성 고유식별자

	private String oauthId;         //구글, 네이버에서 제공하는 고유식별자

	private String name;            //구글, 네이버에서 제공하는 본명

	private String email;           //구글, 네이버에서 제공하는 이메일

	private String username;        //최초회원가입시, 제공되는 랜덤 닉네임

	@Enumerated(EnumType.STRING)    // enum 값을 문자열로 저장
	@Builder.Default
	private Role role = Role.ROLE_USER;              //유저 권한

	private String profileImage;    //사용자 프로필사진

	@Builder.Default
	private boolean withdrawn = false; // 탈퇴 여부

	private LocalDateTime withdrawnAt;//탈퇴 날짜

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false, updatable = false)
	private LocalDateTime updatedAt;
}
