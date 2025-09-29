package com.portfolio.blog.repo;

import com.portfolio.blog.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findBySlug(String slug);

    Optional<Tag> findByNameIgnoreCase(String name);
}