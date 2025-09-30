package com.portfolio.blog.repo;

import com.portfolio.blog.domain.Post;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = {"tags"})
    Optional<Post> findBySlugAndPublishedTrue(String slug);

    @EntityGraph(attributePaths = {"tags"})
    Optional<Post> findBySlug(String slug);

    @EntityGraph(attributePaths = {"tags"})
    Page<Post> findByPublishedTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"tags"})
    Page<Post> findByPublishedTrueAndTitleContainingIgnoreCase(String q, Pageable pageable);
}
