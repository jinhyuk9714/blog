package com.portfolio.blog.web.dto;

import com.portfolio.blog.domain.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

public record CommentResponse(
        @Schema(example = "홍길동") String authorName,
        @Schema(example = "잘 읽었습니다!") String body,
        @Schema(example = "2025-09-01T10:10:00+09:00") OffsetDateTime createdAt
) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(c.getAuthorName(), c.getBody(), c.getCreatedAt());
    }
}