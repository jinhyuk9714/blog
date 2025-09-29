// File: src/main/java/com/portfolio/blog/web/ApiControllers.java
package com.portfolio.blog.web;

import com.portfolio.blog.domain.Comment;
import com.portfolio.blog.service.CommentService;
import com.portfolio.blog.service.PostService;
import com.portfolio.blog.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Public", description = "공개 API")
public class ApiControllers {

    private final PostService posts;
    private final CommentService comments;

    @Operation(summary = "게시글 목록", description = "검색어(q)와 페이지네이션으로 공개 글 목록을 조회합니다.")
    @GetMapping(value = "/posts", produces = "application/json")
    public PageResponse<PostResponse> list(@RequestParam(required = false) String q,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        var pg = posts.listPublished(q, PageRequest.of(Math.max(0, page), Math.min(50, Math.max(1, size))))
                .map(PostResponse::from);
        return PageResponse.of(pg);
    }

    @Operation(summary = "게시글 상세", description = "슬러그로 공개 글 상세를 조회합니다.")
    @GetMapping(value = "/posts/{slug}", produces = "application/json")
    public PostResponse get(@PathVariable String slug) {
        return PostResponse.from(posts.getBySlugPublic(slug));
    }

    // ★ ADD: 댓글 조회
    @Operation(summary = "댓글 목록", description = "해당 글의 승인된 댓글 목록을 조회합니다.")
    @GetMapping(value = "/posts/{slug}/comments", produces = "application/json")
    public List<CommentResponse> listComments(@PathVariable String slug) {
        var post = posts.getBySlugPublic(slug);
        return comments.publicComments(post).stream().map(CommentResponse::from).toList();
    }

    @Operation(summary = "댓글 작성", description = "공개 글에 댓글을 남깁니다.")
    @PostMapping(value = "/posts/{slug}/comments", consumes = "application/json")
    public ResponseEntity<Void> addComment(@PathVariable String slug, @Valid @RequestBody CommentRequest req) {
        var post = posts.getBySlugPublic(slug);
        var c = new Comment();
        c.setPost(post);
        c.setAuthorName(req.authorName());
        c.setBody(req.body());
        var saved = comments.add(c);
        return ResponseEntity.created(URI.create("/api/posts/" + slug + "#comment-" + saved.getId())).build();
    }
}
