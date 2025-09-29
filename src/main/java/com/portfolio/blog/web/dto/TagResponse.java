package com.portfolio.blog.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TagResponse(
        @Schema(example = "Spring") String name,
        @Schema(example = "spring") String slug
) {}