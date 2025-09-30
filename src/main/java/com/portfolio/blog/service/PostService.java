package com.portfolio.blog.service;

import com.portfolio.blog.core.MarkdownService;
import com.portfolio.blog.core.SlugService;
import com.portfolio.blog.domain.Post;
import com.portfolio.blog.domain.Tag;
import com.portfolio.blog.repo.PostRepository;
import com.portfolio.blog.repo.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final MarkdownService markdownService;
    private final SlugService slugService;

    private String uniqueSlug(String base) {
        String s = slugService.toSlug(base);
        if (!postRepository.existsBySlug(s)) return s;
        for (int i=2;; i++) {
            String cand = s + "-" + i;
            if (!postRepository.existsBySlug(cand)) return cand;
        }
    }

    @Transactional
    public Post create(String title, String contentMd, Collection<String> tagNames, boolean publish) {
        Post p = new Post();
        p.setTitle(title);
        p.setSlug(uniqueSlug(title)); // 생성 시 자동
        p.setContentMd(contentMd);
        p.setContentHtml(markdownService.render(contentMd));
        p.setPublished(publish);
        p.setTags(resolveTags(tagNames));
        return postRepository.save(p);
    }

    @Transactional
    public Post updateBySlug(String slug, String title, String contentMd, Collection<String> tagNames, boolean publish) {
        Post p = postRepository.findBySlug(slug).orElseThrow(() -> new NoSuchElementException("Post not found"));
        p.setTitle(title);
        p.setContentMd(contentMd);
        p.setContentHtml(markdownService.render(contentMd));
        p.setPublished(publish);
        p.setTags(resolveTags(tagNames));
        // 슬러그는 유지(불변 정책)
        return p;
    }

    @Transactional
    public void deleteBySlug(String slug) {
        Post p = postRepository.findBySlug(slug).orElseThrow(() -> new NoSuchElementException("Post not found"));
        postRepository.delete(p);
    }

    private Set<Tag> resolveTags(Collection<String> names) {
        if (names == null) return new LinkedHashSet<>();
        Set<Tag> out = new LinkedHashSet<>();
        for (String n : names) {
            if (n == null || n.isBlank()) continue;
            String name = n.trim();
            Tag tag = tagRepository.findByNameIgnoreCase(name).orElseGet(() -> tagRepository.save(new Tag(null, name, null)));
            out.add(tag);
        }
        return out;
    }

    public Page<Post> listPublished(Pageable pageable){
        return postRepository.findByPublishedTrue(pageable);
    }
    public Page<Post> searchPublished(String q, Pageable pageable){
        return postRepository.findByPublishedTrueAndTitleContainingIgnoreCase(q, pageable);
    }
    public Post getBySlugPublic(String slug){
        return postRepository.findBySlugAndPublishedTrue(slug).orElseThrow(() -> new NoSuchElementException("Post not found"));
    }
}