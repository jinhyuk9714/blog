package com.portfolio.blog.service;

import com.portfolio.blog.core.SlugService;
import com.portfolio.blog.domain.Tag;
import com.portfolio.blog.repo.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final SlugService slugService;

    @Transactional
    public Tag findOrCreate(String name) {
        return tagRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            var t = new Tag();
            t.setName(name.trim());
            t.setSlug(slugService.toSlug(name));
            return tagRepository.save(t);
        });
    }
}