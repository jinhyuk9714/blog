package com.portfolio.blog.web.dto;

import com.portfolio.blog.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;
import java.util.*;

public record PostResponse(
        @Schema(example = "스프링 REST 시작하기") String title,
        @Schema(example = "seupring-rest-sijag-hagi", description = "URL 슬러그") String slug,
        @Schema(example = "# 헤딩\n내용 **굵게**") String contentMd,
        @Schema(example = "<h1>헤딩</h1><p>내용 <strong>굵게</strong></p>") String contentHtml,
        @Schema(example = "true") boolean published,
        @Schema(example = "[\"REST\",\"Spring\"]") List<String> tags,
        @Schema(example = "2025-09-01T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(example = "2025-09-01T10:05:00+09:00") OffsetDateTime updatedAt
) {
    public static PostResponse from(Post p) {
        return new PostResponse(
                p.getTitle(), p.getSlug(), p.getContentMd(), p.getContentHtml(),
                p.isPublished(),
                p.getTags().stream().map(t -> t.getName()).sorted().toList(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}