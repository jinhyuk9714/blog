// File: src/main/java/com/portfolio/blog/web/AdminApiController.java
package com.portfolio.blog.web;

import com.portfolio.blog.domain.Post;
import com.portfolio.blog.service.PostService;
import com.portfolio.blog.web.dto.PostRequest;
import com.portfolio.blog.web.dto.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(path = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "admin-api-controller", description = "관리자 API")
public class AdminApiController {

    private final PostService posts;

    @Operation(summary = "게시글 생성", description = "관리자 권한 필요")
    @PostMapping(path = "/posts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest req) {
        Post created = posts.create(req.title(), req.contentMd(), req.tags(), req.published());
        var body = PostResponse.from(created);
        return ResponseEntity
                .created(URI.create("/api/posts/" + body.slug()))
                .body(body);
    }

    @Operation(summary = "게시글 수정", description = "관리자 권한 필요 (제목 변경 시 슬러그도 정책에 따라 재생성됨)")
    @PutMapping(path = "/posts/{slug}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> update(@PathVariable String slug,
                                               @Valid @RequestBody PostRequest req) {
        Post updated = posts.update(slug, req.title(), req.contentMd(), req.tags(), req.published());
        return ResponseEntity.ok(PostResponse.from(updated));
    }

    @Operation(summary = "게시 삭제", description = "관리자 권한 필요")
    @DeleteMapping("/posts/{slug}")
    public ResponseEntity<Void> delete(@PathVariable String slug) {
        posts.deleteBySlug(slug);
        return ResponseEntity.noContent().build();
    }
}
