package com.portfolio.blog.web.dto;

import com.portfolio.blog.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.*;

public record PageResponse<T>(
        @Schema(description = "현재 페이지 아이템") List<T> items,
        @Schema(example = "0") int page,
        @Schema(example = "10") int size,
        @Schema(example = "42") long totalElements,
        @Schema(example = "5") int totalPages
) {
    public static <T> PageResponse<T> of(org.springframework.data.domain.Page<T> page){
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}