package com.portfolio.blog.service;

import com.portfolio.blog.domain.*;
import com.portfolio.blog.repo.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository repo;

    @Transactional(readOnly = true)
    public List<Comment> publicComments(Post post) {
        return repo.findByPostAndApprovedTrue(post);
    }

    @Transactional
    public Comment add(Comment c) {
        return repo.save(c);
    }

    @Transactional
    public void approve(Long id, boolean approved) {
        var c = repo.findById(id).orElseThrow();
        c.setApproved(approved);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
