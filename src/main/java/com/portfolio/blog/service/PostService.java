// File: src/main/java/com/portfolio/blog/service/PostService.java
package com.portfolio.blog.service;

import com.portfolio.blog.core.MarkdownService;
import com.portfolio.blog.core.SlugService;
import com.portfolio.blog.domain.Post;
import com.portfolio.blog.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final TagService tagService;
    private final MarkdownService markdownService;
    private final SlugService slugService;

    @Transactional(readOnly = true)
    public Page<Post> listPublished(String q, Pageable pageable) {
        return (q == null || q.isBlank())
                ? postRepository.findByPublishedTrue(pageable)
                : postRepository.findByPublishedTrueAndTitleContainingIgnoreCase(q.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public Post getBySlugPublic(String slug) {
        return postRepository.findBySlugAndPublishedTrue(slug)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    @Transactional(readOnly = true)
    public Post getBySlugAny(String slug) {
        return postRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    @Transactional
    public Post createOrUpdate(Post input, Set<String> tagNames, boolean publish) {
        // 신규만 슬러그 생성, 수정 시 기존 slug 유지
        if (input.getId() == null) {
            input.setSlug(uniqueSlug(input.getTitle(), null));
        }
        input.setContentHtml(markdownService.render(input.getContentMd()));
        input.setPublished(publish);
        input.setTags(tagNames == null ? Set.of()
                : tagNames.stream().filter(s -> !s.isBlank())
                .map(tagService::findOrCreate).collect(Collectors.toSet()));
        return postRepository.save(input);
    }

    @Transactional
    public void deleteBySlug(String slug) {
        var p = postRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        postRepository.delete(p);
    }

    private String uniqueSlug(String title, Long currentId) {
        String base = slugService.toSlug(title);
        String candidate = base;
        int i = 1;
        while (true) {
            var existing = postRepository.findBySlug(candidate);
            if (existing.isEmpty() || (currentId != null && existing.get().getId().equals(currentId))) {
                return candidate;
            }
            candidate = base + "-" + (++i);
            if (i > 1000) throw new DuplicateKeyException("Too many slug collisions");
        }
    }
}
