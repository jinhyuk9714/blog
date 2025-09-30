package com.portfolio.blog.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.blog.web.dto.PostRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminApiE2ETest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    @DisplayName("관리자 API 생성/수정/삭제 OK")
    void admin_crud_ok() throws Exception {
        // create
        var req = new PostRequest("Admin Post", "# hi", java.util.List.of("Admin"), true);
        var created = om.readTree(
                mvc.perform(post("/api/admin/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(req))
                                .with(httpBasic("admin","admin123")))
                        .andReturn().getResponse().getContentAsByteArray()
        );
        String slug = created.get("slug").asText();
        assertThat(slug).isNotBlank();

        // update (슬러그는 불변 정책)
        var req2 = new PostRequest("Admin Post Edited", "# hi2", java.util.List.of("Admin"), true);
        var updated = om.readTree(
                mvc.perform(put("/api/admin/posts/{slug}", slug)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(req2))
                                .with(httpBasic("admin","admin123")))
                        .andReturn().getResponse().getContentAsByteArray()
        );
        assertThat(updated.get("slug").asText()).isEqualTo(slug); // 슬러그 유지

        // delete
        mvc.perform(delete("/api/admin/posts/{slug}", slug)
                        .with(httpBasic("admin","admin123")))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isEqualTo(204));
    }

}