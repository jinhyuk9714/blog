// File: src/main/java/com/portfolio/blog/service/PostService.java
package com.portfolio.blog.service;

import com.portfolio.blog.core.MarkdownService;
import com.portfolio.blog.core.SlugService;
import com.portfolio.blog.domain.Post;
import com.portfolio.blog.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository posts;
    private final TagService tagService;
    private final SlugService slugService;
    private final MarkdownService markdownService;

    @Transactional(readOnly = true)
    public Page<Post> listPublished(Pageable pageable) {
        return posts.findByPublishedTrueOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> searchPublished(String q, Pageable pageable) {
        return posts.searchPublished(q, pageable);
    }

    @Transactional(readOnly = true)
    public Post getBySlugPublic(String slug) {
        return posts.findBySlugAndPublishedTrue(slug)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    @Transactional
    public Post create(String title, String contentMd, Iterable<String> rawTags, boolean published) {
        Post p = new Post();
        p.setTitle(title);
        p.setSlug(slugService.uniqueSlugForTitle(title)); // 자동 생성
        p.setContentMd(contentMd);
        p.setContentHtml(markdownService.render(contentMd)); // 항상 개행 보장
        p.setPublished(published);
        p.setTags(tagService.resolveTags(rawTags));
        return posts.save(p);
    }

    @Transactional
    public Post update(String currentSlug, String newTitle, String contentMd, Iterable<String> rawTags, boolean published) {
        Post p = posts.findBySlug(currentSlug).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        p.setTitle(newTitle);
        // 정책: 수정 시 슬러그 **유지** (E2E 기대와 일치)
        // 원하면 설정으로 토글 가능하게 확장 가능
        p.setContentMd(contentMd);
        p.setContentHtml(markdownService.render(contentMd)); // 항상 개행 보장
        p.setPublished(published);
        p.setTags(tagService.resolveTags(rawTags));
        return p;
    }

    /** 호환용 alias */
    @Transactional
    public Post updateBySlug(String slug, String title, String contentMd, List<String> tags, boolean published) {
        return update(slug, title, contentMd, tags, published);
    }

    @Transactional
    public void deleteBySlug(String slug) {
        var p = posts.findBySlug(slug).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        posts.delete(p);
    }
}
