package com.portfolio.blog.web.dto;

import com.portfolio.blog.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;

public record PostResponse(
        @Schema String title,
        @Schema String slug,
        @Schema String contentMd,
        @Schema String contentHtml,
        @Schema boolean published,
        @Schema List<String> tags,
        @Schema OffsetDateTime createdAt,
        @Schema OffsetDateTime updatedAt
){
    public static PostResponse from(Post p){
        return new PostResponse(
                p.getTitle(), p.getSlug(), p.getContentMd(), p.getContentHtml(),
                p.isPublished(),
                p.getTags().stream().map(t -> t.getName()).sorted().toList(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}