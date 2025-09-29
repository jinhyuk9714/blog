package com.portfolio.blog.web.dto;

import com.portfolio.blog.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;
import java.util.*;

public record PostRequest(
        @Schema(description = "제목", example = "스프링 REST 시작하기")
        @NotBlank String title,
        @Schema(description = "본문(Markdown)", example = "# 헤딩\n내용 **굵게**")
        @NotBlank String contentMd,
        @Schema(description = "태그 목록(최대 16개)", example = "[\"Spring\",\"REST\"]")
        @Size(max = 16) List<@NotBlank String> tags,
        @Schema(description = "공개 여부", example = "true")
        boolean published
) {
}