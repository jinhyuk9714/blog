// File: src/main/java/com/portfolio/blog/core/SlugService.java
package com.portfolio.blog.core;

import com.portfolio.blog.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SlugService {

    private final PostRepository posts;

    public String toSlug(String input) {
        if (!StringUtils.hasText(input)) return "post";
        String s = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return s.isBlank() ? "post" : s;
    }

    /** 신규 생성용: 기존과 겹치면 -2, -3… 부여 */
    public String uniqueSlugForTitle(String title) {
        return uniqueSlugForTitle(title, null);
    }

    /** 업데이트용: 자신(id) 제외하고 중복 검사 */
    public String uniqueSlugForTitle(String title, Long selfId) {
        String base = toSlug(title);
        String slug = base;
        int i = 2;
        while (existsBySlug(slug, selfId)) {
            slug = base + "-" + i++;
        }
        return slug;
    }

    private boolean existsBySlug(String slug, Long selfId) {
        return posts.findBySlug(slug)
                .map(p -> selfId == null || !p.getId().equals(selfId))
                .orElse(false);
    }
}
