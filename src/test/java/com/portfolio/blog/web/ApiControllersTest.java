// File: src/test/java/com/portfolio/blog/web/ApiControllersTest.java
package com.portfolio.blog.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.blog.domain.Comment;
import com.portfolio.blog.domain.Post;
import com.portfolio.blog.service.CommentService;
import com.portfolio.blog.service.PostService;
import com.portfolio.blog.web.dto.CommentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // ★ ADD
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(ApiControllers.class)
@AutoConfigureMockMvc(addFilters = false) // ★ Security 필터 비활성화(슬라이스 테스트)
class ApiControllersTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean PostService postService;
    @MockitoBean CommentService commentService;

    private Post postEntity(String title, String slug, boolean published) {
        var p = new Post();
        p.setTitle(title);
        p.setSlug(slug);
        p.setContentMd("# hi");
        p.setContentHtml("<h1>hi</h1>");
        p.setPublished(published);
        p.setCreatedAt(OffsetDateTime.now());
        p.setUpdatedAt(OffsetDateTime.now());
        return p;
    }

    @Test @DisplayName("GET /api/posts - 공개 목록 페이지네이션")
    void listPosts() throws Exception {
        var page = new PageImpl<>(List.of(postEntity("t1","t1", true), postEntity("t2","t2", true)),
                PageRequest.of(0,10), 2);
        given(postService.listPublished(isNull(), any(Pageable.class))).willReturn(page);

        mvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].slug").value("t1"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test @DisplayName("GET /api/posts/{slug} - 공개 상세")
    void getPost() throws Exception {
        given(postService.getBySlugPublic("hello")).willReturn(postEntity("hello","hello", true));

        mvc.perform(get("/api/posts/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("hello"))
                .andExpect(jsonPath("$.contentHtml").value("<h1>hi</h1>"));
    }

    @Test @DisplayName("POST /api/posts/{slug}/comments - 유효성 실패 400")
    void addComment_validation() throws Exception {
        given(postService.getBySlugPublic("hello")).willReturn(postEntity("hello","hello", true));

        var body = om.writeValueAsString(new CommentRequest(null, "내용"));
        mvc.perform(post("/api/posts/hello/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test @DisplayName("POST /api/posts/{slug}/comments - 성공 201 + Location 헤더")
    void addComment_ok() throws Exception {
        var p = postEntity("hello","hello", true);
        given(postService.getBySlugPublic("hello")).willReturn(p);
        var saved = new Comment();
        saved.setId(10L);
        given(commentService.add(any())).willReturn(saved);

        var body = om.writeValueAsString(new CommentRequest("me", "good"));
        mvc.perform(post("/api/posts/hello/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/posts/hello#comment-10"));

        assertThat(saved.getId()).isEqualTo(10L);
    }
}
