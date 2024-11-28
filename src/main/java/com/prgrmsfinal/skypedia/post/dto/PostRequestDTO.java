package com.prgrmsfinal.skypedia.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class PostRequestDTO {
    @Data
    @AllArgsConstructor
    public static class Create {
        private String title;

        private String content;

        private String category;

        private Float rate;
    }

    @Data
    @AllArgsConstructor
    public static class Modify {
        private String title;

        private String content;
    }
}
