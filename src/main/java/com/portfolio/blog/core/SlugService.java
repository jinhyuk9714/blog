package com.portfolio.blog.core;

import org.springframework.stereotype.Service;
import java.text.Normalizer;

@Service
public class SlugService {
    // 왜: 서버에서 일관된 URL 정책 보장
    public String toSlug(String title) {
        if (title == null || title.isBlank()) return "post";
        String n = Normalizer.normalize(title, Normalizer.Form.NFD).replaceAll("\\p{M}","");
        String s = n.toLowerCase()
                .replaceAll("[^a-z0-9가-힣\\s-]", "")
                .replaceAll("[\\s_-]+", "-")
                .replaceAll("^-+|-+$", "");
        if (s.isBlank()) s = "post";
        if (s.length() > 80) s = s.substring(0,80).replaceAll("-+$","");
        return s;
    }
}