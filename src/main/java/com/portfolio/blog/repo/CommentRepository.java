package com.portfolio.blog.repo;

import com.portfolio.blog.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostAndApprovedTrue(Post post);
}
