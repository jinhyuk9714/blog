package com.portfolio.blog.web.dto;

import jakarta.validation.constraints.NotBlank;

/** 왜: 미리보기 요청 페이로드 */
public record PreviewRequest(@NotBlank String contentMd) {}