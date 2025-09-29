package com.portfolio.blog.core;

import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class SlugService {
    public String toSlug(String input) {
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String slug = normalized.replaceAll("[^a-zA-Z0-9-]", "").toLowerCase();
        return slug.replaceAll("-{2,}", "-");
    }
}