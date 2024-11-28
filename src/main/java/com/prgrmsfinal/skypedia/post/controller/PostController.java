package com.prgrmsfinal.skypedia.post.controller;

import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
import com.prgrmsfinal.skypedia.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<?> read(Authentication authentication, @PathVariable Long postId) {
        return ResponseEntity.ok(Map.of(
                "message", "게시글을 성공적으로 조회했습니다.",
                "result", postService.read(authentication, postId)
        ));
    }

    @GetMapping("s/{lastPostId}")
    public ResponseEntity<?> readAll(@RequestParam("category") String category
            , @RequestParam("order") String order
            , @RequestParam(name = "last", defaultValue = "0") Long lastPostId) {
        return ResponseEntity.ok(Map.of(
                "message", "게시글 목록을 성공적으로 조회했습니다.",
                "result", postService.readAll(category, lastPostId, order)
        ));
    }

    @PostMapping
    public ResponseEntity<?> create(Authentication authentication, PostRequestDTO.Create request) {
        postService.create(authentication, request);

        return ResponseEntity.ok(Map.of("message", "게시글을 성공적으로 등록했습니다."));
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<?> toggleLikes(Authentication authentication, @PathVariable Long postId) {
        return ResponseEntity.ok(Map.of("message", postService.toggleLikes(authentication, postId)
                ? "게시글에 좋아요를 눌렀습니다." : "게시글에 좋아요를 취소했습니다."));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> modify(Authentication authentication, @PathVariable Long postId, @RequestBody PostRequestDTO.Modify request) {
        postService.modify(authentication, postId, request);

        return ResponseEntity.ok(Map.of("message", "게시글을 성공적으로 수정했습니다."));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(Authentication authentication, @PathVariable Long postId) {
        postService.delete(authentication, postId);

        return ResponseEntity.ok(Map.of("message", "게시글을 성공적으로 삭제했습니다."));
    }

    @PatchMapping("/{postId}/restore")
    public ResponseEntity<?> restore(Authentication authentication, @PathVariable Long postId) {
        postService.restore(authentication, postId);

        return ResponseEntity.ok(Map.of("message", "게시글을 성공적으로 복원했습니다."));
    }
}
