// File: src/main/java/com/portfolio/blog/repo/PostRepository.java
package com.portfolio.blog.repo;

import com.portfolio.blog.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    Optional<Post> findBySlugAndPublishedTrue(String slug);

    @Query("""
           select p from Post p
           where p.published = true
           order by p.createdAt desc
           """)
    Page<Post> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
           select p from Post p
           where p.published = true
             and ( lower(p.title) like lower(concat('%', :q, '%'))
                   or lower(p.contentMd) like lower(concat('%', :q, '%')) )
           order by p.createdAt desc
           """)
    Page<Post> searchPublished(@Param("q") String q, Pageable pageable);
}
