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
@Tag(name="Public", description="공개 API")
public class ApiControllers {
    private final PostService posts;
    private final CommentService comments;

    @Operation(summary="게시글 목록", description="검색어(q)/페이지네이션")
    @GetMapping(value="/posts", produces="application/json")
    public PageResponse<PostResponse> list(@RequestParam(required=false) String q,
                                           @RequestParam(defaultValue="0") int page,
                                           @RequestParam(defaultValue="10") int size){
        var pg = (q == null || q.isBlank())
                ? posts.listPublished(PageRequest.of(Math.max(0,page), Math.min(50, Math.max(1,size))))
                : posts.searchPublished(q, PageRequest.of(Math.max(0,page), Math.min(50, Math.max(1,size))));
        return PageResponse.of(pg.map(PostResponse::from));
    }

    @Operation(summary="게시글 상세")
    @GetMapping(value="/posts/{slug}", produces="application/json")
    public PostResponse get(@PathVariable String slug){
        return PostResponse.from(posts.getBySlugPublic(slug));
    }

    @Operation(summary="댓글 목록(승인된 것만)")
    @GetMapping(value="/posts/{slug}/comments", produces="application/json")
    public List<CommentResponse> listComments(@PathVariable String slug){
        var post = posts.getBySlugPublic(slug);
        return comments.publicComments(post).stream().map(CommentResponse::from).toList();
    }

    @Operation(summary="댓글 작성")
    @PostMapping(value="/posts/{slug}/comments", consumes="application/json")
    public ResponseEntity<Void> addComment(@PathVariable String slug, @Valid @RequestBody CommentRequest req){
        var post = posts.getBySlugPublic(slug);
        var c = new Comment();
        c.setPost(post); c.setAuthorName(req.authorName()); c.setBody(req.body());
        var saved = comments.add(c);
        return ResponseEntity.created(URI.create("/api/posts/"+slug+"#comment-"+saved.getId())).build();
    }
}