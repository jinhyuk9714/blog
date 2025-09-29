package com.portfolio.blog.web.dto;

import com.portfolio.blog.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.*;

public record CommentRequest(
        @Schema(example = "홍길동") @NotBlank String authorName,
        @Schema(example = "잘 읽었습니다!") @NotBlank String body
) {}