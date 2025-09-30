// File: src/main/java/com/portfolio/blog/web/AdminPreviewController.java
package com.portfolio.blog.web;

import com.portfolio.blog.core.MarkdownService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

record PreviewRequest(@NotBlank String contentMd) {}
record PreviewResponse(String html) {}

@RestController
@RequestMapping(path = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminPreviewController {

    private final MarkdownService markdown;

    @PostMapping(path = "/preview", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PreviewResponse> preview(@Valid @RequestBody PreviewRequest req) {
        // 왜: 서버와 동일 파이프라인으로 렌더링 보장
        String html = markdown.render(req.contentMd());
        return ResponseEntity.ok(new PreviewResponse(html));
    }
}
