// File: src/test/java/com/portfolio/blog/web/ApiControllersTest.java
package com.portfolio.blog.web;

import com.portfolio.blog.domain.Post;
import com.portfolio.blog.service.CommentService;
import com.portfolio.blog.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ★ 핵심: 프레임워크 패키지의 MockitoBean 사용
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest({ApiControllers.class, RestExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class ApiControllersTest {

    @Autowired MockMvc mvc;

    @MockitoBean PostService postService;
    @MockitoBean CommentService commentService;

    private Post post(String slug, String title) {
        Post p = new Post();
        p.setSlug(slug);
        p.setTitle(title);
        p.setPublished(true);
        return p;
    }

    @Test
    @DisplayName("GET /api/posts - 공개 목록 페이지네이션")
    void list() throws Exception {
        var page = new PageImpl<>(List.of(post("t1","t1"), post("t2","t2")),
                PageRequest.of(0,10), 2);
        BDDMockito.given(postService.listPublished(any(Pageable.class))).willReturn(page);

        mvc.perform(get("/api/posts").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].slug").value("t1"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}
