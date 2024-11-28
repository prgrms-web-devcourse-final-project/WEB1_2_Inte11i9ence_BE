package com.prgrmsfinal.skypedia.post.dto;

import com.prgrmsfinal.skypedia.post.entity.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {
    @Getter
    @Builder
    public static class Read {
        private Long id;

        private String title;

        private String content;

        private Long views;

        private Long likes;

        private float rating;

        private boolean liked;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        // ※ 추후 사진 도메인과 연동 필요함!!!

        private List<String> images;

        public static PostResponseDTO.Read byEntity(Post post) {
            return PostResponseDTO.Read.builder()
                    .id(post.getId())
                    .title(post.getContent())
                    .content(post.getContent())
                    .views(post.getViews())
                    .likes(post.getLikes())
                    .rating(post.getRating())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }

        public void setViewsAndLikes(Long views, Long likes, boolean liked) {
            this.views = views;
            this.likes = likes;
            this.liked = liked;
        }
    }
}
