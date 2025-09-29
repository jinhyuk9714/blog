// File: src/main/java/com/portfolio/blog/web/TagApiController.java
package com.portfolio.blog.web;

import com.portfolio.blog.repo.TagRepository;
import com.portfolio.blog.web.dto.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagApiController {
    private final TagRepository tags;

    @GetMapping("/tags")
    public List<TagResponse> tags() {
        return tags.findAll()
                .stream()
                .map(t -> new TagResponse(t.getName(), t.getSlug()))
                .toList();
    }
}
