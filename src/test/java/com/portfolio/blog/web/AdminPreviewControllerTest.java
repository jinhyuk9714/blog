package com.portfolio.blog.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.blog.core.MarkdownService;
import com.portfolio.blog.web.dto.PreviewRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// WHY: Controller 슬라이스 테스트. MarkdownService만 실제 Bean 주입.
@WebMvcTest(AdminPreviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MarkdownService.class)
class AdminPreviewControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @Test
    void preview_returns_html() throws Exception {
        var req = new PreviewRequest("# 제목\n내용 **굵게**");
        mvc.perform(post("/api/admin/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.html", containsString("<h1>제목</h1>")))
                .andExpect(jsonPath("$.html", containsString("<strong>굵게</strong>")));
    }
}
