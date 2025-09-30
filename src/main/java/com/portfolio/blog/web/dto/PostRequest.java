package com.portfolio.blog.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PostRequest(
        @Schema(description = "제목") @NotBlank String title,
        @Schema(description = "본문(Markdown)") @NotBlank String contentMd,
        @Schema(description = "태그 목록(최대 16개)") @Size(max=16) List<String> tags,
        @Schema(description = "공개 여부") boolean published
) {} // 왜: slug는 받지 않음(서버 자동 생성)