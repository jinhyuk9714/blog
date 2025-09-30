// File: src/main/java/com/portfolio/blog/service/TagService.java
package com.portfolio.blog.service;

import com.portfolio.blog.core.SlugService;
import com.portfolio.blog.domain.Tag;
import com.portfolio.blog.repo.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tags;
    private final SlugService slugService;

    /** 입력 문자열 집합을 정규화하여(소문자/트리밍) Tag 엔티티 셋으로 반환. 중복 삽입 방지. */
    @Transactional
    public Set<Tag> resolveTags(Iterable<String> raw) {
        Set<Tag> result = new LinkedHashSet<>();
        if (raw == null) return result;

        for (String s : raw) {
            if (s == null) continue;
            String name = s.trim();
            if (name.isEmpty()) continue;

            String normalizedName = name.toLowerCase(Locale.ROOT);
            String slug = slugService.toSlug(normalizedName);

            Tag tag = tags.findBySlug(slug).orElseGet(() -> {
                Tag t = new Tag();
                t.setName(normalizedName);
                t.setSlug(slug);
                return tags.save(t); // 존재하지 않을 때만 insert
            });
            result.add(tag);
        }
        return result;
    }
}
