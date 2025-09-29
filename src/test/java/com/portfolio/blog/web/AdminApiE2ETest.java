// File: src/test/java/com/portfolio/blog/web/AdminApiE2ETest.java
package com.portfolio.blog.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.blog.web.dto.PostRequest;
import com.portfolio.blog.web.dto.PostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminApiE2ETest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test @DisplayName("관리자 API 생성/수정/삭제 OK")
    void admin_crud_ok() throws Exception {
        var basic = SecurityMockMvcRequestPostProcessors.httpBasic("admin","admin123");

        // 1) CREATE
        var create = new PostRequest("admin post", "# md", List.of("x","y"), true);
        MvcResult createRes = mvc.perform(post("/api/admin/posts")
                        .with(basic)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(create)))
                .andReturn();
        var created = om.readValue(createRes.getResponse().getContentAsString(), PostResponse.class);
        var slug = created.slug();
        assertThat(slug).isNotBlank();

        // 2) UPDATE (제목 변경 → 슬러그도 변경됨)
        var update = new PostRequest("admin post edited", "content **md**", List.of("y"), true);
        MvcResult updateRes = mvc.perform(put("/api/admin/posts/{slug}", slug)
                        .with(basic)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(update)))
                .andReturn();
        assertThat(updateRes.getResponse().getStatus()).isEqualTo(200);
        var updated = om.readValue(updateRes.getResponse().getContentAsString(), PostResponse.class);
        assertThat(updated.title()).isEqualTo("admin post edited");

        // ★ 슬러그 갱신 (중요)
        slug = updated.slug();
        assertThat(slug).isEqualTo("admin-post-edited"); // 정책 확인용; 정책 바꾸면 이 줄은 지워도 됨

        // 3) DELETE (갱신된 슬러그로 삭제)
        MvcResult deleteRes = mvc.perform(delete("/api/admin/posts/{slug}", slug).with(basic))
                .andReturn();
        assertThat(deleteRes.getResponse().getStatus()).isEqualTo(204);
    }
}
