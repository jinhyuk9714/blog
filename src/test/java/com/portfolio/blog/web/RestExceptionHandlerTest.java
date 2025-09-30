// File: src/test/java/com/portfolio/blog/web/RestExceptionHandlerTest.java
package com.portfolio.blog.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.blog.domain.Post;
import com.portfolio.blog.service.CommentService;
import com.portfolio.blog.service.PostService;
import com.portfolio.blog.web.dto.CommentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ★ 핵심: 프레임워크 패키지의 MockitoBean 사용
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest({ApiControllers.class, RestExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class RestExceptionHandlerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean PostService postService;
    @MockitoBean CommentService commentService;

    @Test
    void validation_error_shape() throws Exception {
        var p = new Post();
        p.setTitle("t"); p.setSlug("t");
        p.setContentMd("a"); p.setContentHtml("a");
        p.setPublished(true);
        p.setCreatedAt(OffsetDateTime.now());
        p.setUpdatedAt(OffsetDateTime.now());
        // 댓글 작성 시, 404 대신 검증 에러를 보려면 게시글 조회는 성공하도록 목
        org.mockito.BDDMockito.given(postService.getBySlugPublic("t")).willReturn(p);

        var bad = new CommentRequest("", "");
        mvc.perform(post("/api/posts/t/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.errors.authorName").exists())
                .andExpect(jsonPath("$.details.errors.body").exists());
    }
}
