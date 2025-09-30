package com.portfolio.blog.web;

import com.portfolio.blog.domain.Post;
import com.portfolio.blog.service.PostService;
import com.portfolio.blog.web.dto.PostRequest;
import com.portfolio.blog.web.dto.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class AdminApiController {

    private final PostService posts;

    @Operation(summary="게시글 생성(슬러그 자동 생성)")
    @PostMapping("/posts")
    public ResponseEntity<PostResponse> create(@RequestBody PostRequest req){
        Post created = posts.create(req.title(), req.contentMd(), req.tags(), req.published());
        return ResponseEntity.status(201).body(PostResponse.from(created));
    }

    @Operation(summary="게시글 수정(슬러그 유지)")
    @PutMapping("/posts/{slug}")
    public ResponseEntity<PostResponse> update(@PathVariable String slug, @RequestBody PostRequest req){
        Post updated = posts.updateBySlug(slug, req.title(), req.contentMd(), req.tags(), req.published());
        return ResponseEntity.ok(PostResponse.from(updated));
    }

    @Operation(summary="게시글 삭제")
    @DeleteMapping("/posts/{slug}")
    public ResponseEntity<Void> delete(@PathVariable String slug){
        posts.deleteBySlug(slug);
        return ResponseEntity.noContent().build();
    }
}