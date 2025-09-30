// File: src/main/java/com/portfolio/blog/repo/TagRepository.java
package com.portfolio.blog.repo;

import com.portfolio.blog.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findBySlug(String slug);
}
