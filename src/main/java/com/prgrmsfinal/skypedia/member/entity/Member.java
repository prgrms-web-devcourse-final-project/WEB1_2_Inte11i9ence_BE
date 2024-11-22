package com.prgrmsfinal.skypedia.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                //자동생성 고유식별자

    private String oauthId;         //구글, 네이버에서 제공하는 고유식별자

    @Column(nullable = false)
    private String name;            //구글, 네이버에서 제공하는 본명

    private String email;           //구글, 네이버에서 제공하는 이메일

    @Column(unique = true)
    private String username;        //최초회원가입시, 제공되는 랜덤 닉네임

    private String role;            //유저 권한

    private String profileImage;    //사용자 프로필사진

    @CreatedDate
    private LocalDateTime createdAt;//회원가입 날짜
    @LastModifiedDate
    private LocalDateTime updatedAt;//업데이트 날짜

    private boolean withdrawn;      //탈퇴여부

    @LastModifiedDate
    private LocalDateTime withdrawnAt;//탈퇴 날짜


}
