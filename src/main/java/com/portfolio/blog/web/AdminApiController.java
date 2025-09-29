// File: src/main/java/com/portfolio/blog/web/AdminApiController.java
package com.portfolio.blog.web;

import com.portfolio.blog.domain.Post;
import com.portfolio.blog.service.PostService;
import com.portfolio.blog.web.dto.PostRequest;
import com.portfolio.blog.web.dto.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "basicAuth") // Swagger UI에서 Authorize 버튼으로 Basic 인증
public class AdminApiController {

    private final PostService posts;

    @Operation(summary = "게시글 생성", description = "관리자 권한 필요")
    @ApiResponse(responseCode = "201", description = "생성됨")
    @PostMapping(value = "/posts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest req) {
        var p = new Post();
        p.setTitle(req.title());
        p.setContentMd(req.contentMd());
        var tagSet = req.tags() == null ? Set.<String>of() : Set.copyOf(req.tags());
        var saved = posts.createOrUpdate(p, tagSet, req.published());
        return ResponseEntity.created(URI.create("/api/posts/" + saved.getSlug())).body(PostResponse.from(saved));
    }

    @Operation(summary = "게시글 수정", description = "관리자 권한 필요 (제목 변경 시 슬러그도 변경)")
    @ApiResponse(responseCode = "200", description = "수정됨")
    @PutMapping(value = "/posts/{slug}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostResponse update(@PathVariable String slug, @Valid @RequestBody PostRequest req) {
        var p = posts.getBySlugAny(slug);
        p.setTitle(req.title());
        p.setContentMd(req.contentMd());
        var tagSet = req.tags() == null ? Set.<String>of() : req.tags().stream().collect(Collectors.toSet());
        var saved = posts.createOrUpdate(p, tagSet, req.published());
        return PostResponse.from(saved);
    }

    @Operation(summary = "게시글 삭제", description = "관리자 권한 필요")
    @ApiResponse(responseCode = "204", description = "삭제됨")
    @DeleteMapping("/posts/{slug}")
    public ResponseEntity<Void> delete(@PathVariable String slug) {
        posts.deleteBySlug(slug);
        return ResponseEntity.noContent().build();
    }
}
