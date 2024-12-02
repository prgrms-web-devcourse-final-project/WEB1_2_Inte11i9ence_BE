package com.prgrmsfinal.skypedia.post.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post-category")
@Tag(name = "게시글 카테고리 API 컨트롤러", description = "게시글 카테고리와 관련된 REST API를 제공하는 컨트롤러입니다.")
public class PostCategoryController {

}
